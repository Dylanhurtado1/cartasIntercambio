package com.example.cartasIntercambio.controller;

import com.example.cartasIntercambio.dto.OfertaDto;
import com.example.cartasIntercambio.dto.PublicacionDto;
import com.example.cartasIntercambio.model.Mercado.Filtros;
import com.example.cartasIntercambio.model.Mercado.Publicacion;
import com.example.cartasIntercambio.service.OfertaServiceImpl;
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

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/publicaciones")
public class PublicacionController{
    private final PublicacionServiceImpl publicacionService;
    private final OfertaServiceImpl ofertaService;
    private final ObjectMapper mapper = new ObjectMapper();


    @Autowired
    public PublicacionController(PublicacionServiceImpl publicacionService, OfertaServiceImpl ofertaService) {
        this.publicacionService = publicacionService;
        this.ofertaService = ofertaService;
    }

    //TODO: Paginacion
    @GetMapping
    public ResponseEntity<List<PublicacionDto>> listarPublicaciones() {
        List<PublicacionDto> publicaciones = publicacionService.listarPublicaciones();

        return new ResponseEntity<>(publicaciones, HttpStatus.OK);
    }

    @GetMapping("/{idPublicacion}")
    public ResponseEntity<Publicacion> buscarPublicacion(@PathVariable("idPublicacion") Long idPublicacion) {
        Optional<Publicacion> publicacion = publicacionService.buscarPublicacionPorId(idPublicacion);

        return publicacion.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<PublicacionDto> crearPublicacion(@RequestBody PublicacionDto publicacionDto) {
        publicacionService.guardarPublicacion(publicacionDto);

        return new ResponseEntity<>(publicacionDto, HttpStatus.CREATED);
    }

    @PostMapping("/busqueda")
    public ResponseEntity<List<PublicacionDto>> buscarPor(@RequestBody Filtros filtros) {
        List<PublicacionDto> publicaciones = publicacionService.listarPublicaciones();
        if(filtros.getNombreJuego() != null) publicaciones = publicaciones.stream().filter(p -> p.getCartaOfrecida().getJuego().toString().equalsIgnoreCase(filtros.getNombreJuego())).toList();
        if(filtros.getNombreCarta() != null) publicaciones = publicaciones.stream().filter(p -> p.getCartaOfrecida().getNombre().equalsIgnoreCase(filtros.getNombreCarta())).toList();
        if(filtros.getEstado() != null) publicaciones = publicaciones.stream().filter(p -> p.getCartaOfrecida().getEstado().toString().equalsIgnoreCase(filtros.getEstado())).toList();
        if(filtros.getPrecioMin() != null) publicaciones = publicaciones.stream().filter(p -> p.getPrecio().compareTo(filtros.getPrecioMin()) > 0).toList();
        if(filtros.getPrecioMax() != null) publicaciones = publicaciones.stream().filter(p -> p.getPrecio().compareTo(filtros.getPrecioMax()) < 0).toList();

        return new ResponseEntity<>(publicaciones, HttpStatus.OK);
    }


    @PostMapping("/{idPublicacion}/ofertas")
    public ResponseEntity<OfertaDto> crearOferta(@PathVariable("idPublicacion") Long idPublicacion, @RequestBody OfertaDto ofertaDto) {
        ofertaService.crearOferta(ofertaDto, idPublicacion);

        return new ResponseEntity<>(ofertaDto, HttpStatus.CREATED);
    }

//    @GetMapping("/nombre/{nombre}")
//    public ResponseEntity<List<PublicacionDto>> buscarPublicacionPorNombreDeCarta(@PathVariable String nombre) {
//        return new ResponseEntity<>(publicacionService.buscarPublicacionPorNombre(nombre), HttpStatus.OK);
//    }
//
//    @GetMapping("/juego/{juego}")
//    public ResponseEntity<List<PublicacionDto>> buscarPublicacionCartaPorNombreDeJuego(@PathVariable String juego) {
//        return new ResponseEntity<>(publicacionService.buscarPublicacionPorJuego(juego), HttpStatus.OK);
//    }
//
//    @GetMapping("/estado/{estado}")
//    public ResponseEntity<List<PublicacionDto>> buscarPublicacionPorEstadoDeCarta(@PathVariable String estado) {
//        return new ResponseEntity<>(publicacionService.buscarPublicacionPorEstadoDeCarta(estado), HttpStatus.OK);
//    }
//
//    @GetMapping("/precio/{precio}")
//    public ResponseEntity<List<PublicacionDto>> buscarPublicacionPorPrecioDeCarta(@PathVariable BigDecimal precio) {

//        return new ResponseEntity<>(publicacionService.buscarPublicacionPorPrecio(precio), HttpStatus.OK);

//    }

    @GetMapping("/usuarios/{idUsuario}") // TODO: El ID del usuario no lo vamos a pasar por URL
    public ResponseEntity<List<PublicacionDto>> listarPublicacionesPorUsuario(@PathVariable("idUsuario") Long idUsuario) {
        //  Queda pendiente resolver eso, paso provisoriamente el id por parametro para que no tire error.
        List<PublicacionDto> publicacionesDTO = publicacionService.buscarPublicacionesPorUsuario(idUsuario);

        return new ResponseEntity<>(publicacionesDTO, HttpStatus.OK);
    }

    // Ofertas recibidas para una publicacion del usuario logueado
//    @GetMapping("/{idPublicacion}/ofertas/{idUsuario}") // TODO: El ID del usuario no lo vamos a pasar por URL
//    public ResponseEntity<List<OfertaDto>> listarOfertasRecibidas(@PathVariable("idPublicacion") Long idPublicacion, @PathVariable("idUsuario") Long idUsuario) {
//        List<OfertaDto> ofertasRecibidas = publicacionService.buscarOfertasPorPublicacion(idPublicacion, idUsuario);
//
//        return new ResponseEntity<>(ofertasRecibidas, HttpStatus.OK);
//    }

//    Aceptar o rechazar una oferta
//Si la url es directamente con el id de la oferta => Oferta oferta = findById(idOferta);
    //TODO: Logica en el service
//    @PatchMapping(path = "/{idPublicacion}/ofertas/{idUsuario}/{idOferta}", consumes = "application/json-patch+json")
//    public ResponseEntity<OfertaDto> responderOferta(@PathVariable("idPublicacion") Long idPublicacion, @PathVariable("idUsuario") Long idUsuario, @PathVariable("idOferta") Long idOferta, @RequestBody JsonPatch patch) {
//        List<OfertaDto> ofertasRecibidas = publicacionService.buscarOfertasPorPublicacion(idPublicacion, idUsuario);
//        //OfertaDto oferta = ofertasRecibidas.stream().filter(unaOferta -> unaOferta.getId().equals(idOferta)).findAny().orElse(null);
//        OfertaDto oferta = ofertasRecibidas.stream().findAny().orElse(null);
//        try {
//            OfertaDto ofertaActualizada = patchOferta(patch, oferta);
//            //TODO: Rechazar el resto de ofertas de esta publicacion
//            publicacionService.actualizarOferta(ofertaActualizada);
//            return new ResponseEntity<>(ofertaActualizada, HttpStatus.OK);
//        } catch (JsonPatchException | JsonProcessingException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//
//    private OfertaDto patchOferta (JsonPatch patch, OfertaDto oferta) throws JsonPatchException, JsonProcessingException {
//        JsonNode patched = patch.apply(mapper.convertValue(oferta, JsonNode.class));
//        return mapper.treeToValue(patched, OfertaDto.class);
//    }
//
    }