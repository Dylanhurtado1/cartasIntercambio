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
import java.util.ArrayList;
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

    // Buscar todas las publicaciones con o sin filtros
    @GetMapping
    public ResponseEntity<List<PublicacionDto>> listarPublicaciones(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String juego,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) BigDecimal preciomin,
            @RequestParam(required = false) BigDecimal preciomax
    ) {
        return ResponseEntity.ok(
                publicacionService.buscarPublicacionesFiltradas(nombre, juego, estado, preciomin, preciomax)
        );
    }

    @GetMapping("/{idPublicacion}")
    public ResponseEntity<PublicacionDto> buscarPublicacion(@PathVariable("idPublicacion") String idPublicacion) {
        PublicacionDto publicacionDto = publicacionService.buscarPublicacionDTOPorId(idPublicacion);
        return new ResponseEntity<>(publicacionDto, HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<PublicacionDto> crearPublicacion(@RequestBody PublicacionDto publicacionDto) {
        PublicacionDto guardada= publicacionService.guardarPublicacion(publicacionDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    // Crear una oferta para una publicacion
    @PostMapping("/{idPublicacion}/ofertas")
    public ResponseEntity<OfertaDto> crearOferta(@PathVariable("idPublicacion") String idPublicacion, @RequestBody OfertaDto ofertaDto) {
        Publicacion publicacion = publicacionService.buscarPublicacionPorId(idPublicacion);
        OfertaDto dtoGuardado = ofertaService.crearOferta(ofertaDto, publicacion);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoGuardado);
    }

    //Buscar todas las ofertas de una publicacion
    @GetMapping("/{idPublicacion}/ofertas")
    public ResponseEntity<List<OfertaDto>> buscarOfertasDeUnaPublicacion(@PathVariable("idPublicacion") String idPublicacion) {
        List<OfertaDto> ofertas = ofertaService.buscarOfertasPorPublicacion(idPublicacion);
        return ResponseEntity.ok(ofertas);
    }

    // Buscar una oferta de una publicacion
    @GetMapping("/ofertas/{idOferta}")
    public ResponseEntity<OfertaDto> buscarOferta(@PathVariable("idOferta") String idOferta) {
        OfertaDto oferta = ofertaService.buscarOfertaDto(idOferta);
        return new ResponseEntity<>(oferta, HttpStatus.OK);
    }


    // TODO: Chequear si una publicacion esta finalizada. Front??
    // Aceptar o rechazar una oferta
    @PatchMapping(path = "/ofertas/{idOferta}", consumes = "application/json-patch+json")
    public ResponseEntity<OfertaDto> responderOferta(@PathVariable("idOferta") String idOferta, @RequestBody JsonPatch patch) {
        Oferta oferta = ofertaService.buscarOfertaPorId(idOferta);
        try {
            Oferta ofertaActualizada = patchOferta(patch, oferta);
            ofertaService.guardarOferta(ofertaActualizada);
            if(ofertaActualizada.getEstado().equals(EstadoOferta.ACEPTADO)){
                ofertaService.rechazarOtrasOfertas(idOferta, ofertaActualizada.getIdPublicacion());
                publicacionService.finalizarPublicacion(ofertaActualizada.getIdPublicacion());
            }
            return ResponseEntity.ok(new OfertaDto(ofertaActualizada));
        } catch (JsonPatchException | JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private Oferta patchOferta (JsonPatch patch, Oferta oferta) throws JsonPatchException, JsonProcessingException {
        JsonNode patched = patch.apply(mapper.convertValue(oferta, JsonNode.class));
        return mapper.treeToValue(patched, Oferta.class);
    }

    // Mis publicaciones
    @GetMapping("/usuario/{idUsuario}") // TODO: El ID del usuario no lo vamos a pasar por URL
    public ResponseEntity<List<PublicacionDto>> listarPublicacionesPorUsuario(@PathVariable("idUsuario") String idUsuario) {
        List<PublicacionDto> publicacionesDTO = publicacionService.buscarPublicacionesPorUsuario(idUsuario);
        return ResponseEntity.ok(publicacionesDTO);
    }

    // Mis ofertas recibidas
    @GetMapping("/usuario/{idUsuario}/ofertas/recibidas") // TODO: El ID del usuario no lo vamos a pasar por URL
    public ResponseEntity<List<OfertaDto>> listarOfertasRecibidas(@PathVariable("idUsuario") String idUsuario) {
        List<PublicacionDto> publicacionesUsuario = publicacionService.buscarPublicacionesPorUsuario(idUsuario);
        List<OfertaDto> ofertasRecibidas = new ArrayList<>();
        for(PublicacionDto publicacion : publicacionesUsuario){
            ofertasRecibidas.addAll(ofertaService.buscarOfertasPorPublicacion(publicacion.getId()));
        }
        return ResponseEntity.ok(ofertasRecibidas);
    }

    // Mis ofertas realizadas
    @GetMapping("/usuario/{idUsuario}/ofertas/realizadas")
    public ResponseEntity<List<OfertaDto>> listarOfertasRealizadas(@PathVariable("idUsuario") String idUsuario) {
        List<OfertaDto> ofertasRealizadas = ofertaService.buscarOfertasRealizadas(idUsuario);
        return ResponseEntity.ok(ofertasRealizadas);
    }

}