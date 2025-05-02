package com.example.cartasIntercambio.controller;

import com.example.cartasIntercambio.dto.PublicacionDto;
import com.example.cartasIntercambio.model.Producto_Carta.Carta;
import com.example.cartasIntercambio.service.PublicacionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/estadisticas")
public class EstadisticasController {

    private final PublicacionServiceImpl publicacionService;

    @Autowired
    public EstadisticasController(PublicacionServiceImpl publicacionService) {
        this.publicacionService = publicacionService;
    }

    //No se si hacia falta un controller, fue mas que nada por la URL. Podria agregarse a
    // al controller de publicaciones "publicaciones/estadisticas" y lesto.
    //TODO: Chequear que sea un usuario admin
    @GetMapping
    public ResponseEntity<Map<String, Integer>> estadisticas() {
        List<PublicacionDto> publicaciones = publicacionService.listarPublicaciones();
        Map<String, Integer> stats = Map.of(
                "Total", publicaciones.size(),
                "Magic", contarPublicacionesPorJuego(publicaciones, "Magic"),
                "Pokemon", contarPublicacionesPorJuego(publicaciones, "Pokemon"),
                "YuGiOh", contarPublicacionesPorJuego(publicaciones, "YuGiOh")
        );
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    public Integer contarPublicacionesPorJuego(List<PublicacionDto> publicaciones, String juego) {
        Integer count = 0;
        for (PublicacionDto publicacion : publicaciones) {
                if(publicacion.getCartaOfrecida().getJuego().toString().equalsIgnoreCase(juego)) count++;
        }
        return count;
    }


}