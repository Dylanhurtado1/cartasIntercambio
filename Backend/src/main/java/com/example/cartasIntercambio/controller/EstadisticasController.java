package com.example.cartasIntercambio.controller;

import com.example.cartasIntercambio.service.PublicacionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/estadisticas")
public class EstadisticasController {

    private final PublicacionServiceImpl publicacionService;

    @Autowired
    public EstadisticasController(PublicacionServiceImpl publicacionService) {
        this.publicacionService = publicacionService;
    }

    //TODO: Chequear que sea un usuario admin
    @GetMapping
    public ResponseEntity<Map<String, Integer>> estadisticas() {

        return new ResponseEntity<>(publicacionService.contarPublicacionesPorJuego(), HttpStatus.OK);
    }


}