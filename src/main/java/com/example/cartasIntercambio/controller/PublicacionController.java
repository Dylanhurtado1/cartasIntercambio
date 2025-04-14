package com.example.cartasIntercambio.controller;

import com.example.cartasIntercambio.dto.OfertaDto;
import com.example.cartasIntercambio.dto.PublicacionDto;
import com.example.cartasIntercambio.service.PublicacionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/publicaciones")
public class PublicacionController {
    private final PublicacionServiceImpl publicacionService;

    @Autowired
    public PublicacionController(PublicacionServiceImpl publicacionService) {
        this.publicacionService = publicacionService;
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
    public ResponseEntity<List<PublicacionDto>> buscarPublicacionPorPrecioDeCarta(@PathVariable float precio) {
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

    @GetMapping
    public ResponseEntity<List<PublicacionDto>> listarPublicacionesPorUsuario(Long idUsuario) {
        // TODO: Asumo que el ID del usuario no lo vamos a pasar por URL.
        //  Queda pendiente resolver eso, paso provisoriamente el id por parametro para que no tire error.
        List<PublicacionDto> publicaciones = publicacionService.buscarPublicacionesPorUsuario(idUsuario);

        return new ResponseEntity<>(publicaciones, HttpStatus.OK);
    }

    @GetMapping("/{idPublicacion}/ofertas") // Ofertas recibidas para una publicacion del usuario logueado
    public ResponseEntity<List<OfertaDto>> listarOfertasRecibidas(@PathVariable("idPublicacion") Long idPublicacion, Long idUsuario) {
        // TODO: Asumo que el ID del usuario no lo vamos a pasar por URL.
        List<OfertaDto> ofertasRecibidas = publicacionService.buscarOfertasPorPublicacion(idPublicacion, idUsuario);

        return new ResponseEntity<>(ofertasRecibidas, HttpStatus.OK);
    }

}