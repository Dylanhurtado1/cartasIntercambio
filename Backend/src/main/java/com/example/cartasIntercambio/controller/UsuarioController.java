package com.example.cartasIntercambio.controller;

import com.example.cartasIntercambio.dto.UsuarioDto;
import com.example.cartasIntercambio.dto.UsuarioResponseDto;
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
    public ResponseEntity<List<UsuarioResponseDto>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> buscarUsuarioPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarUsuarioPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> actualizarUsuario(@PathVariable Long id, @RequestBody UsuarioDto usuarioDto) {
        return ResponseEntity.ok(usuarioService.actualizarUsuario(id, usuarioDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrarUsuario(@PathVariable Long id) {
        usuarioService.borrarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<UsuarioResponseDto>> buscarUsuarios(
            @RequestParam(required = false) String user,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String correo
    ) {
        List<UsuarioResponseDto> resultado = usuarioService.buscarUsuarios(user, nombre, correo);
        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/admins")
    public ResponseEntity<UsuarioResponseDto> crearAdmin(@RequestBody UsuarioDto dto) {
        UsuarioResponseDto resp = usuarioService.crearAdmin(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

}
