package com.example.cartasIntercambio.controller;

import com.example.cartasIntercambio.jwt.JwtUtil;
import com.example.cartasIntercambio.model.Usuario.Usuario;
import com.example.cartasIntercambio.repository.irepository.IUsuarioRepository;
import com.example.cartasIntercambio.service.PublicacionServiceImpl;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/estadisticas")
public class EstadisticasController {

    private final PublicacionServiceImpl publicacionService;
    private final JwtUtil jwtUtil;
    private final IUsuarioRepository usuarioRepository;
    @Autowired
    public EstadisticasController(PublicacionServiceImpl publicacionService, JwtUtil jwtUtil, IUsuarioRepository usuarioRepository) {
        this.publicacionService = publicacionService;
        this.jwtUtil = jwtUtil;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public ResponseEntity<Map<String, Integer>> estadisticas(@CookieValue("jwt") String token) {

        Claims claims = jwtUtil.validateToken(token);
        String userId = claims.getSubject();

        Optional<Usuario> usuario = usuarioRepository.findById(userId);
        //Solo se puede editar el tipo de usuario desde la base de datos
        if (usuario.isEmpty() || !usuario.get().getTipo().equals("ADMIN")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(publicacionService.contarPublicacionesPorJuego(), HttpStatus.OK);
    }


}