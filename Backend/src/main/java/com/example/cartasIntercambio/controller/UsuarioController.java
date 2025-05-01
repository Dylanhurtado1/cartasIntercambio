package com.example.cartasIntercambio.controller;

import com.example.cartasIntercambio.dto.UsuarioDto;
import com.example.cartasIntercambio.service.UsuarioServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioServiceImpl usuarioService;

    @Autowired
    public UsuarioController(UsuarioServiceImpl usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioDto> registrarUsuario(@RequestBody UsuarioDto usuarioDto) {
        usuarioService.registrarUsuario(usuarioDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioDto);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDto>> listarUsuarios() {
        usuarioService.listarUsuarios();
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }




}
