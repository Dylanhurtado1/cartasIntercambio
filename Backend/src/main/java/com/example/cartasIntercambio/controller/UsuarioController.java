package com.example.cartasIntercambio.controller;

import com.example.cartasIntercambio.dto.UsuarioDto;
import com.example.cartasIntercambio.dto.UsuarioResponseDto;
import com.example.cartasIntercambio.jwt.JwtUtil;
import com.example.cartasIntercambio.service.UsuarioServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioServiceImpl usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    public UsuarioController(UsuarioServiceImpl usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDto> registrarUsuario(@RequestBody UsuarioDto usuarioDto) {
        UsuarioResponseDto creado = usuarioService.registrarUsuario(usuarioDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDto>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> buscarUsuarioPorId(@PathVariable String id) {
        return ResponseEntity.ok(usuarioService.buscarUsuarioPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> actualizarUsuario(@PathVariable String id, @RequestBody UsuarioDto usuarioDto) {
        UsuarioResponseDto actualizado = usuarioService.actualizarUsuario(id, usuarioDto);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrarUsuario(@PathVariable String id) {
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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UsuarioDto loginDTO) {
        UsuarioResponseDto user = usuarioService.login(loginDTO);
        if(user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login inválido");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUser());
        return ResponseEntity.ok(Map.of(
                "token", token,
                "usuario", user
        ));
    }
}
