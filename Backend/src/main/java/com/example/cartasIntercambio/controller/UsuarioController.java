package com.example.cartasIntercambio.controller;

import com.example.cartasIntercambio.dto.UsuarioDto;
import com.example.cartasIntercambio.dto.UsuarioResponseDto;
import com.example.cartasIntercambio.jwt.JwtUtil;
import com.example.cartasIntercambio.model.Usuario.Admin;
import com.example.cartasIntercambio.model.Usuario.Usuario;
import com.example.cartasIntercambio.service.UsuarioServiceImpl;
import com.example.cartasIntercambio.util.GoogleVerifierUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioServiceImpl usuarioService;

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

    @GetMapping("/whoami")
    public Map<String, Object> whoami(OAuth2AuthenticationToken authToken) {
        return Map.of("principal", authToken.getPrincipal().getAttributes());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UsuarioDto loginDTO) {
        UsuarioResponseDto user = usuarioService.login(loginDTO);
        if(user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login inválido");
        }
        String token = JwtUtil.generateToken(user.getId(), user.getUser());
        // Devolvé el token Y el usuario
        return ResponseEntity.ok(Map.of(
                "token", token,
                "usuario", user
        ));
    }

    @PostMapping("/oauth2/login")
    public ResponseEntity<?> loginConGoogle(@RequestBody Map<String, String> body) {
        String idTokenString = body.get("id_token");
        var payload = GoogleVerifierUtil.verify(idTokenString);

        if (payload == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token de Google inválido");
        }

        String email = payload.getEmail();
        String nombre = (String) payload.get("given_name");
        String apellido = (String) payload.get("family_name");

        // Esto lo implementás en el service, ver abajo
        Usuario usuario = usuarioService.buscarOCrearPorEmail(email, nombre, apellido);

        UsuarioResponseDto userDto = UsuarioResponseDto.builder()
                .id(usuario.getId())
                .user(usuario.getUser())
                .nombre(usuario.getNombre())
                .correo(usuario.getEmail())
                .tipo(usuario instanceof Admin ? "admin" : "usuario")
                .build();

        String token = JwtUtil.generateToken(usuario.getId(), usuario.getUser());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "usuario", userDto
        ));
    }

}
