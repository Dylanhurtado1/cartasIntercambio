package com.example.cartasIntercambio.controller;

import com.example.cartasIntercambio.dto.UsuarioDto;
import com.example.cartasIntercambio.dto.UsuarioResponseDto;
import com.example.cartasIntercambio.exception.UsuarioNoEncontradoException;
import com.example.cartasIntercambio.exception.UsuarioYaExisteException;
import com.example.cartasIntercambio.jwt.JwtUtil;
import com.example.cartasIntercambio.model.Usuario.Usuario;
import com.example.cartasIntercambio.repository.irepository.IOfertaRepository;
import com.example.cartasIntercambio.repository.irepository.IPublicacionRepository;
import com.example.cartasIntercambio.repository.irepository.IUsuarioRepository;
import com.example.cartasIntercambio.service.UsuarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    private JwtUtil jwtUtil;

    @MockBean
    private MongoTemplate mongoTemplate;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

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
    void testListarUsuarios() throws Exception {
        UsuarioResponseDto adminResponseDto1 = adminAUsuarioResponseDTO(admin1);
        UsuarioResponseDto usuarioResponseDto2 = usuarioAUsuarioResponseDTO(usuario2);
        UsuarioResponseDto usuarioResponseDto3 = usuarioAUsuarioResponseDTO(usuario3);

        when(usuarioService.listarUsuarios()).thenReturn(List.of(adminResponseDto1, usuarioResponseDto2, usuarioResponseDto3));

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(3))
                .andExpect(jsonPath("$[0].id").value(adminResponseDto1.getId()))
                .andExpect(jsonPath("$[0].user").value(adminResponseDto1.getUser()))
                .andExpect(jsonPath("$[0].correo").value(adminResponseDto1.getCorreo()))
                .andExpect(jsonPath("$[0].tipo").value("admin"))
                .andExpect(jsonPath("$[1].id").value(usuarioResponseDto2.getId()))
                .andExpect(jsonPath("$[1].user").value(usuarioResponseDto2.getUser()))
                .andExpect(jsonPath("$[1].correo").value(usuarioResponseDto2.getCorreo()))
                .andExpect(jsonPath("$[1].tipo").value("usuario"))
                .andExpect(jsonPath("$[2].id").value(usuarioResponseDto3.getId()))
                .andExpect(jsonPath("$[2].user").value(usuarioResponseDto3.getUser()))
                .andExpect(jsonPath("$[2].correo").value(usuarioResponseDto3.getCorreo()))
                .andExpect(jsonPath("$[2].tipo").value("usuario"));
    }

    @Test
    void testListarUsuarios_Vacio() throws Exception {
        when(usuarioService.listarUsuarios()).thenReturn(List.of());

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testBuscarUsuarioPorId() throws Exception {
        UsuarioResponseDto usuarioResponseDto2 = usuarioAUsuarioResponseDTO(usuario2); // tiene id 2

        when(usuarioService.buscarUsuarioPorId("2")).thenReturn(usuarioResponseDto2);

        mockMvc.perform(get("/api/usuarios/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("2"))
                .andExpect(jsonPath("$.user").value(usuario2.getUser()))
                .andExpect(jsonPath("$.nombre").value(usuario2.getNombre()))
                .andExpect(jsonPath("$.correo").value(usuario2.getEmail()))
                .andExpect(jsonPath("$.tipo").value("usuario"));
    }

    @Test
    void testBuscarUsuarioPorId_NoEncontrado_LanzaExcepcion() throws Exception {
        when(usuarioService.buscarUsuarioPorId("10"))
                .thenThrow(new UsuarioNoEncontradoException("Usuario con id 10 no existe"));

        mockMvc.perform(get("/api/usuarios/10"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testBuscarUsuariosConFiltros() throws Exception { // filtro por user
        UsuarioResponseDto usuarioResponseDto3 = usuarioAUsuarioResponseDTO(usuario3);

        when(usuarioService.buscarUsuarios(eq("pedro25"), isNull(), isNull()))
                .thenReturn(List.of(usuarioResponseDto3));

        mockMvc.perform(get("/api/usuarios/search")
                        .param("user", "pedro25"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("3"))
                .andExpect(jsonPath("$[0].user").value("pedro25"))
                .andExpect(jsonPath("$[0].nombre").value("Pedro"))
                .andExpect(jsonPath("$[0].correo").value("pgonzales@cartas.com"))
                .andExpect(jsonPath("$[0].tipo").value("usuario"));
    }

    @Test
    void testBuscarUsuariosConFiltrosVacio() throws Exception {
        UsuarioResponseDto adminResponseDto1 = adminAUsuarioResponseDTO(admin1);
        UsuarioResponseDto usuarioResponseDto2 = usuarioAUsuarioResponseDTO(usuario2);
        UsuarioResponseDto usuarioResponseDto3 = usuarioAUsuarioResponseDTO(usuario3);
        UsuarioResponseDto usuarioResponseDto4 = usuarioAUsuarioResponseDTO(usuario4);

        when(usuarioService.buscarUsuarios(isNull(), isNull(), isNull()))
                .thenReturn(List.of(adminResponseDto1, usuarioResponseDto2, usuarioResponseDto3, usuarioResponseDto4));

        mockMvc.perform(get("/api/usuarios/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].nombre").value("Admin"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].nombre").value("Juan"))
                .andExpect(jsonPath("$[2].id").value("3"))
                .andExpect(jsonPath("$[2].nombre").value("Pedro"))
                .andExpect(jsonPath("$[3].id").value("4"))
                .andExpect(jsonPath("$[3].nombre").value("Kaila"));
    }

    @Test
    void testCrearAdmin() throws Exception {
        UsuarioDto adminDTO = usuarioAUsuarioDTO(admin1);
        UsuarioResponseDto adminResponseDTO = adminAUsuarioResponseDTO(admin1);

        when(usuarioService.crearAdmin(any(UsuarioDto.class))).thenReturn(adminResponseDTO);

        mockMvc.perform(post("/api/usuarios/admins")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(adminResponseDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.user").value("ADMINUSER"))
                .andExpect(jsonPath("$.nombre").value("Admin"))
                .andExpect(jsonPath("$.correo").value("admin@cartas.com"))
                .andExpect(jsonPath("$.tipo").value("admin"));
    }

    @Test
    void testCrearAdmin_YaExistente_LanzaExcepcion() throws Exception {
        UsuarioDto adminDTO = usuarioAUsuarioDTO(admin1);

        when(usuarioService.crearAdmin(any(UsuarioDto.class)))
                .thenThrow(new UsuarioYaExisteException("Ya existe un usuario (o admin) con ese user y/o correo"));

        mockMvc.perform(post("/api/usuarios/admins")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(adminDTO)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Ya existe un usuario (o admin) con ese user y/o correo"));
    }

//    @Test
//    void testLogin() throws Exception {
//        UsuarioDto usuarioDto2 = usuarioAUsuarioDTO(usuario2);
//        UsuarioResponseDto usuarioResponseDto2 = usuarioAUsuarioResponseDTO(usuario2);
//
//        when(usuarioService.login(any(UsuarioDto.class))).thenReturn(usuarioResponseDto2);
//        when(jwtUtil.generateToken("2", "juanpe80")).thenReturn("test-jwt-token");
//
//        mockMvc.perform(post("/api/usuarios/login")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(usuarioDto2)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.token").value("test-jwt-token"))
//                .andExpect(jsonPath("$.usuario.id").value("2"))
//                .andExpect(jsonPath("$.usuario.user").value("juanpe80"))
//                .andExpect(jsonPath("$.usuario.nombre").value("Juan"))
//                .andExpect(jsonPath("$.usuario.correo").value("jperez@cartas.com"))
//                .andExpect(jsonPath("$.usuario.tipo").value("usuario"));
//    }

//    @Test
//    void testLogin_CredencialesIncorrectas_LanzaExcepcion() throws Exception {
//        UsuarioDto usuarioDto2 = usuarioAUsuarioDTO(usuario2);
//        usuarioDto2.setPassword("passwordInvalida");
//
//        when(usuarioService.login(any(UsuarioDto.class))).thenReturn(null);
//
//        mockMvc.perform(post("/api/usuarios/login")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(usuarioDto2)))
//                .andExpect(status().isUnauthorized())
//                .andExpect(content().string("Login inválido"));
//    }

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