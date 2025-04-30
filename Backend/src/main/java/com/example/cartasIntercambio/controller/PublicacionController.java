package com.example.cartasIntercambio.controller;

import com.example.cartasIntercambio.dto.OfertaDto;
import com.example.cartasIntercambio.dto.PublicacionDto;
import com.example.cartasIntercambio.model.Mercado.Oferta;
import com.example.cartasIntercambio.model.Mercado.Publicacion;
import com.example.cartasIntercambio.service.PublicacionServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/publicaciones")
public class PublicacionController {
    private final PublicacionServiceImpl publicacionService;


    @Autowired
    public PublicacionController(PublicacionServiceImpl publicacionService) {
        this.publicacionService = publicacionService;
    }

    @GetMapping
    public ResponseEntity<List<PublicacionDto>> listarPublicaciones() {
        List<PublicacionDto> publicaciones = publicacionService.listarPublicaciones();
        return new ResponseEntity<>(publicaciones, HttpStatus.OK);
    }
    
    @GetMapping("/nombre/{nombre}") 
    public ResponseEntity<List<PublicacionDto>> buscarPublicacionPorNombreDeCarta(@PathVariable String nombre) {
        return new ResponseEntity<>(publicacionService.buscarPublicacionPorNombre(nombre), HttpStatus.OK);
    }

    @GetMapping("/juego/{juego}") 
    public ResponseEntity<List<PublicacionDto>> buscarPublicacionCartaPorNombreDeJuego(@PathVariable String juego) {
        return new ResponseEntity<>(publicacionService.buscarPublicacionPorJuego(juego), HttpStatus.OK);
    }

    @GetMapping("/estado/{estado}") 
    public ResponseEntity<List<PublicacionDto>> buscarPublicacionPorEstadoDeCarta(@PathVariable String estado) {
        return new ResponseEntity<>(publicacionService.buscarPublicacionPorEstadoDeCarta(estado), HttpStatus.OK);
    }

    @GetMapping("/precio/{precio}") 
    public ResponseEntity<List<PublicacionDto>> buscarPublicacionPorPrecioDeCarta(@PathVariable BigDecimal precio) {
        return new ResponseEntity<>(publicacionService.buscarPublicacionPorPrecio(precio), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PublicacionDto> crearPublicacion(@RequestBody PublicacionDto publicacionDto) {
        publicacionService.guardarPublicacion(publicacionDto);
        return new ResponseEntity<>(publicacionDto, HttpStatus.CREATED);
    }

    @PostMapping("/{idPublicacion}/ofertas")
    public ResponseEntity<OfertaDto> crearOferta(@PathVariable("idPublicacion") Long idPublicacion, @RequestBody OfertaDto ofertaDto) {
        publicacionService.crearOferta(idPublicacion, ofertaDto);

        return new ResponseEntity<>(ofertaDto, HttpStatus.CREATED);
    }

    @GetMapping("/usuarios/{idUsuario}") // TODO: El ID del usuario no lo vamos a pasar por URL
    public ResponseEntity<List<PublicacionDto>> listarPublicacionesPorUsuario(@PathVariable("idUsuario") Long idUsuario) {
        //  Queda pendiente resolver eso, paso provisoriamente el id por parametro para que no tire error.
        List<PublicacionDto> publicacionesDTO = publicacionService.buscarPublicacionesPorUsuario(idUsuario);

        return new ResponseEntity<>(publicacionesDTO, HttpStatus.OK);
    }

    // Ofertas recibidas para una publicacion del usuario logueado
    @GetMapping("/{idPublicacion}/ofertas/{idUsuario}") // TODO: El ID del usuario no lo vamos a pasar por URL
    public ResponseEntity<List<OfertaDto>> listarOfertasRecibidas(@PathVariable("idPublicacion") Long idPublicacion, @PathVariable("idUsuario") Long idUsuario) {
        List<OfertaDto> ofertasRecibidas = publicacionService.buscarOfertasPorPublicacion(idPublicacion, idUsuario);

        return new ResponseEntity<>(ofertasRecibidas, HttpStatus.OK);
    }

    //Aceptar o rechazar una oferta
//    @PatchMapping(path = "/{idPublicacion}/ofertas/{idUsuario}/{idOferta}", consumes = "application/json-patch+json")
//    public ResponseEntity<OfertaDto> responderOferta(@PathVariable("idPublicacion") Long idPublicacion, @PathVariable("idUsuario") Long idUsuario, @PathVariable("idOferta") Long idOferta, @RequestBody JsonPatch patch) {
//        List<OfertaDto> ofertasRecibidas = publicacionService.buscarOfertasPorPublicacion(idPublicacion, idUsuario);
//        OfertaDto oferta = ofertasRecibidas.stream().filter(unaOferta -> unaOferta.getId().equals(idOferta)).findAny().orElse(null);
//        //Si la url es directamente con el id de la oferta => Oferta oferta = findById(idOferta);
//        try {
//            OfertaDto ofertaActualizada = patchOferta(patch, oferta);
//            publicacionService.actualizarOferta(ofertaActualizada);
//            return new ResponseEntity<>(ofertaActualizada, HttpStatus.OK);
//        } catch (JsonPatchException | JsonProcessingException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }

    @PutMapping(path = "/{idPublicacion}/ofertas/{idUsuario}/{idOferta}")
    public ResponseEntity<OfertaDto> rresponderOferta(@PathVariable("idPublicacion") Long idPublicacion, @PathVariable("idUsuario") Long idUsuario, @PathVariable("idOferta") Long idOferta, @RequestBody String respuesta) {
        List<OfertaDto> ofertasRecibidas = publicacionService.buscarOfertasPorPublicacion(idPublicacion, idUsuario);
        OfertaDto oferta = ofertasRecibidas.stream().findAny().orElse(null);//TODO: Deberia buscar por id
        //Si la url es directamente con el id de la oferta => Oferta oferta = findById(idOferta);
        try {
            oferta.setEstado(respuesta);
            publicacionService.actualizarOferta(oferta);
            return new ResponseEntity<>(oferta, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

//    private OfertaDto patchOferta (JsonPatch patch, OfertaDto oferta) throws JsonPatchException, JsonProcessingException {
//        ObjectMapper mapper = new ObjectMapper();
//        JsonNode patched = patch.apply(mapper.convertValue(oferta, JsonNode.class));
//        return mapper.treeToValue(patched, OfertaDto.class);
//    }

    @GetMapping("estadisticas/publicaciones")
    public ResponseEntity<Integer> estadisticasPublicaciones() {
        List<PublicacionDto> publicaciones = publicacionService.listarPublicaciones();
        return new ResponseEntity<>(publicaciones.size(), HttpStatus.OK);
    }

    @GetMapping("estadisticas/juegos")
    public ResponseEntity<Map<String, Integer>> estadisticasPublicacionesPorJuego() {
        List<PublicacionDto> publicacionesMagic = publicacionService.buscarPublicacionPorJuego("Magic");
        List<PublicacionDto> publicacionesPokemon = publicacionService.buscarPublicacionPorJuego("Pokemon");
        List<PublicacionDto> publicacionesYuGiOh = publicacionService.buscarPublicacionPorJuego("YuGiOh");
        Map<String, Integer> counts = Map.of(
                "Magic", publicacionesMagic.size(),
                "Pokemon", publicacionesPokemon.size(),
                "YuGiOh", publicacionesYuGiOh.size()
        );
        return new ResponseEntity<>(counts, HttpStatus.OK);
    }

}