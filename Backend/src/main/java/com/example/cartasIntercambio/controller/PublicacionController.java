package com.example.cartasIntercambio.controller;

import com.example.cartasIntercambio.dto.OfertaDto;
import com.example.cartasIntercambio.dto.PublicacionDto;
import com.example.cartasIntercambio.model.Mercado.EstadoOferta;
import com.example.cartasIntercambio.model.Mercado.Oferta;
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

import java.math.BigDecimal;
import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<PublicacionDto>> listarPublicaciones(
        @RequestParam(required = false) String nombre,
        @RequestParam(required = false) String juego,
        @RequestParam(required = false) String estado,
        @RequestParam(required = false) BigDecimal preciomin,
        @RequestParam(required = false) BigDecimal preciomax
    ) {
        List<PublicacionDto> publicaciones = publicacionService.listarPublicaciones();
        if(nombre != null) publicaciones = publicaciones.stream().filter(p -> p.getCartaOfrecida().getNombre().toLowerCase().contains(nombre.toLowerCase())).toList();
        if(juego != null) publicaciones = publicaciones.stream().filter(p -> p.getCartaOfrecida().getJuego().toLowerCase().contains(juego.toLowerCase())).toList();
        if(estado != null) publicaciones = publicaciones.stream().filter(p -> p.getCartaOfrecida().getEstado().toString().toLowerCase().contains(estado.toLowerCase())).toList();
        if(preciomin != null) publicaciones = publicaciones.stream().filter(p -> p.getPrecio().compareTo(preciomin) > 0).toList();
        if(preciomax != null) publicaciones = publicaciones.stream().filter(p -> p.getPrecio().compareTo(preciomax) < 0).toList();
        return new ResponseEntity<>(publicaciones, HttpStatus.OK);
    }

    @GetMapping("/{idPublicacion}")
    public ResponseEntity<PublicacionDto> buscarPublicacion(@PathVariable("idPublicacion") Long idPublicacion) {
        PublicacionDto publicacionDto = publicacionService.buscarPublicacionDTOPorId(idPublicacion);

        return new ResponseEntity<>(publicacionDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PublicacionDto> crearPublicacion(@RequestBody PublicacionDto publicacionDto) {
        publicacionService.guardarPublicacion(publicacionDto);

        return new ResponseEntity<>(publicacionDto, HttpStatus.CREATED);
    }

    @PostMapping("/{idPublicacion}/ofertas")
    public ResponseEntity<OfertaDto> crearOferta(@PathVariable("idPublicacion") Long idPublicacion, @RequestBody OfertaDto ofertaDto) {
        Publicacion publicacion = publicacionService.buscarPublicacionPorId(idPublicacion);
        ofertaService.crearOferta(ofertaDto, publicacion);

        return new ResponseEntity<>(ofertaDto, HttpStatus.CREATED);
    }

    @GetMapping("/{idPublicacion}/ofertas")
    public ResponseEntity<List<OfertaDto>> buscarOfertasDeUnaPublicacion(@PathVariable("idPublicacion") Long idPublicacion) {
        List<OfertaDto> ofertas = ofertaService.buscarOfertasPorPublicacion(idPublicacion);

        if(ofertas == null) return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(ofertas, HttpStatus.OK);
    }

    //Es solo para chequear que se creen las ofertas
    @GetMapping("/ofertas/{idOferta}")
    public ResponseEntity<OfertaDto> buscarOferta(@PathVariable("idOferta") Long idOferta) {
        OfertaDto oferta = ofertaService.buscarOfertaDto(idOferta);

        return new ResponseEntity<>(oferta, HttpStatus.OK);
    }


//    TODO: Chequear si una publicacion esta finalizada. Front??
//    Aceptar o rechazar una oferta
    @PatchMapping(path = "/ofertas/{idOferta}", consumes = "application/json-patch+json")
    public ResponseEntity<Oferta> responderOferta(@PathVariable("idOferta") Long idOferta, @RequestBody JsonPatch patch) {
        Oferta oferta = ofertaService.buscarOfertaPorId(idOferta);
        try {
            Oferta ofertaActualizada = patchOferta(patch, oferta);
            ofertaService.guardarOferta(ofertaActualizada);
            if(ofertaActualizada.getEstado().equals(EstadoOferta.ACEPTADO)){
                ofertaService.rechazarOtrasOfertas(idOferta, ofertaActualizada.getIdPublicacion());
                publicacionService.finalizarPublicacion(ofertaActualizada.getIdPublicacion());
            }
            return new ResponseEntity<>(ofertaActualizada, HttpStatus.OK);
        } catch (JsonPatchException | JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private Oferta patchOferta (JsonPatch patch, Oferta oferta) throws JsonPatchException, JsonProcessingException {
        JsonNode patched = patch.apply(mapper.convertValue(oferta, JsonNode.class));
        return mapper.treeToValue(patched, Oferta.class);
    }

    // Mis publicaciones (publicaciones del usuario logueado)
    @GetMapping("/usuarios/{idUsuario}") // TODO: El ID del usuario no lo vamos a pasar por URL
    public ResponseEntity<List<PublicacionDto>> listarPublicacionesPorUsuario(@PathVariable("idUsuario") Long idUsuario) {
        //  Queda pendiente resolver eso, paso provisoriamente el id por parametro para que no tire error.
        List<PublicacionDto> publicacionesDTO = publicacionService.buscarPublicacionesPorUsuario(idUsuario);

        return new ResponseEntity<>(publicacionesDTO, HttpStatus.OK);
    }

    // Ofertas recibidas para una publicacion del usuario logueado
    @GetMapping("/{idPublicacion}/ofertas/{idUsuario}") // TODO: El ID del usuario no lo vamos a pasar por URL
    public ResponseEntity<List<OfertaDto>> listarOfertasRecibidas(@PathVariable("idPublicacion") Long idPublicacion, @PathVariable("idUsuario") Long idUsuario) {
        Publicacion publicacion = publicacionService.buscarPublicacionPorId(idPublicacion);
        List<OfertaDto> ofertasRecibidas = ofertaService.buscarOfertasPorPublicacion(publicacion, idUsuario);

        return new ResponseEntity<>(ofertasRecibidas, HttpStatus.OK);
    }

    // Ofertas hechas por el usuario logueado a publicaciones de otros usuarios
    @GetMapping("/usuarios/{idUsuario}/ofertas")
    public ResponseEntity<List<OfertaDto>> listarOfertasRealizadas(@PathVariable("idUsuario") Long idUsuario) {
        List<OfertaDto> ofertasRealizadas = ofertaService.buscarOfertasRealizadas(idUsuario);

        return new ResponseEntity<>(ofertasRealizadas, HttpStatus.OK);
    }

}