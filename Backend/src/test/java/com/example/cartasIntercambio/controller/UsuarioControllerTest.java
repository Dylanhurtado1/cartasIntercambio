package com.example.cartasIntercambio.controller;

import com.example.cartasIntercambio.dto.UsuarioDto;
import com.example.cartasIntercambio.dto.UsuarioResponseDto;
import com.example.cartasIntercambio.exception.UsuarioYaExisteException;
import com.example.cartasIntercambio.jwt.JwtUtil;
import com.example.cartasIntercambio.model.Usuario.Usuario;
import com.example.cartasIntercambio.repository.irepository.IOfertaRepository;
import com.example.cartasIntercambio.repository.irepository.IPublicacionRepository;
import com.example.cartasIntercambio.repository.irepository.IUsuarioRepository;
import com.example.cartasIntercambio.service.CookieService;
import com.example.cartasIntercambio.service.UsuarioServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UsuarioControllerTest {
    private Usuario admin1;
    private Usuario usuario2;
    private Usuario usuario3;
    private Usuario usuario4;

    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioServiceImpl usuarioService;

    @MockBean
    private IUsuarioRepository usuarioRepository;

    @MockBean
    private IOfertaRepository ofertaRepository;

    @MockBean
    private IPublicacionRepository publicacionRepository;

    @MockBean
    private MongoTemplate mongoTemplate;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CookieService cookieService;

    @MockBean
    private HttpServletResponse response;

    @BeforeEach
    void init() throws ParseException {
        // Admins
        admin1 = this.crearUsuarioEjemplo("1", "Admin", "User", "ADMINUSER", "admin@cartas.com", "M0r1t42013", "2013-11-13");

        // Usuarios
        usuario2 = this.crearUsuarioEjemplo("2", "Juan", "Perez", "juanpe80", "jperez@cartas.com", "JuanPe!2025", "1980-05-04");
        usuario3 = this.crearUsuarioEjemplo("3", "Pedro", "Gonzales", "pedro25", "pgonzales@cartas.com", "P3dr0!", "1996-08-11");
        usuario4 = this.crearUsuarioEjemplo("4", "Kaila", "Rodriguez", "Kailaa", "krodriguez@cartas.com", "K41l1t4!", "2000-05-23");

        objectMapper = new ObjectMapper();
    }

    // ---------------------------------------- TESTS ---------------------------------------- //

    @Test
    void testRegistrarUsuario() throws Exception {
        UsuarioDto usuarioDto2 = usuarioAUsuarioDTO(usuario2);
        UsuarioResponseDto usuarioResponseDto2 = usuarioAUsuarioResponseDTO(usuario2);

        when(usuarioService.registrarUsuario(any(UsuarioDto.class)))
                .thenReturn(usuarioResponseDto2);

        mockMvc.perform(post("/api/usuarios")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(usuarioDto2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("2"))
                .andExpect(jsonPath("$.user").value("juanpe80"))
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.correo").value("jperez@cartas.com"))
                .andExpect(jsonPath("$.tipo").value("usuario"));
    }

    @Test
    void testRegistrarUsuario_YaExiste_LanzaExcepcion() throws Exception {
        UsuarioDto usuarioDto2 = usuarioAUsuarioDTO(usuario2);

        // Simulamos que el service lanza la excepción por usuario ya existente
        when(usuarioService.registrarUsuario(any(UsuarioDto.class)))
                .thenThrow(new UsuarioYaExisteException("Ya existe un usuario con ese user y/o correo"));

        mockMvc.perform(post("/api/usuarios")
                        .contentType("application/json")
                        .content(asJsonString(usuarioDto2)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UsuarioYaExisteException))
                .andExpect(result -> assertEquals(
                        "Ya existe un usuario con ese user y/o correo",
                        result.getResolvedException().getMessage()
                ));
    }

    @Test
    void testLogin() throws Exception {
        UsuarioDto usuarioDto2 = usuarioAUsuarioDTO(usuario2);
        UsuarioResponseDto usuarioResponseDto2 = usuarioAUsuarioResponseDTO(usuario2);

        when(usuarioService.login(any(UsuarioDto.class), any(HttpServletResponse.class))).thenReturn(usuarioResponseDto2);

        mockMvc.perform(post("/api/usuarios/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(usuarioDto2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("2"))
                .andExpect(jsonPath("$.user").value("juanpe80"))
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.correo").value("jperez@cartas.com"))
                .andExpect(jsonPath("$.tipo").value("usuario"));
    }

    @Test
    void testLogin_CredencialesIncorrectas_LanzaExcepcion() throws Exception {
        UsuarioDto usuarioDto2 = usuarioAUsuarioDTO(usuario2);
        usuarioDto2.setPassword("passwordInvalida");

        when(usuarioService.login(any(UsuarioDto.class), any(HttpServletResponse.class))).thenReturn(null);

        mockMvc.perform(post("/api/usuarios/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(usuarioDto2)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Login inválido"));
    }

    @Test
    void testLogout() throws Exception {

        doNothing().when(usuarioService).logout(any(HttpServletResponse.class));

        mockMvc.perform(post("/api/usuarios/logout"))
                .andExpect(status().isOk())
                .andExpect(content().string("Sesion cerrada correctamente"));
    }


    // -------------------------------------------------------------------------------------- //
    // --------------------------------- Metodos Auxiliares --------------------------------- //

    private Usuario crearUsuarioEjemplo(String id, String nombre, String apellido, String user, String email, String password, String fecha) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaNacimiento = sdf.parse(fecha);

        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setUser(user);
        usuario.setEmail(email);
        usuario.setPassword(password);
        usuario.setFechaNacimiento(fechaNacimiento);

        return usuario;
    }

    // Creacion DTOs

    public UsuarioDto usuarioAUsuarioDTO(Usuario usuario) {

        UsuarioDto usuarioDTO = new UsuarioDto(
                usuario.getUser(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getPassword()
        );

        return usuarioDTO;
    }

    public UsuarioResponseDto usuarioAUsuarioResponseDTO(Usuario usuario) {

        UsuarioResponseDto usuarioResponseDTO = new UsuarioResponseDto(
                usuario.getId(),
                usuario.getUser(),
                usuario.getNombre(),
                usuario.getEmail(),
                "usuario"
        );

        return usuarioResponseDTO;
    }

    public UsuarioResponseDto adminAUsuarioResponseDTO(Usuario admin) {

        UsuarioResponseDto usuarioResponseDTO = new UsuarioResponseDto(
                admin.getId(),
                admin.getUser(),
                admin.getNombre(),
                admin.getEmail(),
                "admin"
        );

        return usuarioResponseDTO;
    }

    private static String asJsonString(final Object obj) {
        try {
            //return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(obj);
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}