package com.example.cartasIntercambio.controller;

import com.example.cartasIntercambio.dto.UsuarioResponseDto;
import com.example.cartasIntercambio.jwt.JwtUtil;
import com.example.cartasIntercambio.model.Usuario.Usuario;
import com.example.cartasIntercambio.repository.irepository.IOfertaRepository;
import com.example.cartasIntercambio.repository.irepository.IPublicacionRepository;
import com.example.cartasIntercambio.repository.irepository.IUsuarioRepository;
import com.example.cartasIntercambio.service.PublicacionServiceImpl;
import com.example.cartasIntercambio.service.UsuarioServiceImpl;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EstadisticasController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EstadisticasControllerTest {
    private Usuario admin;
    private Usuario usuario;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioServiceImpl usuarioService;

    @MockBean
    private PublicacionServiceImpl publicacionService;

    @MockBean
    private IPublicacionRepository publicacionRepository;

    @MockBean
    private IOfertaRepository ofertaRepository;

    @MockBean
    private IUsuarioRepository usuarioRepository;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void init() throws ParseException {
        // usuarios
        admin = this.crearUsuarioEjemplo("1", "Admin", "Superuser", "ADMINUSER", "admin@cartas.com", "admin123", "2013-11-13");
        usuario = this.crearUsuarioEjemplo("2", "Juan", "Perez", "juanpe80", "jperez@cartas.com", "JuanPe!2025", "1980-05-04");
    }

    // ---------------------------------------- TESTS ---------------------------------------- //
    @Test
    void testObtenerEstadisticas() throws Exception {
        // Obtenemos un contador general de cantidad de publicaciones y uno por cada juego
        Map<String, Integer> mockStats = Map.of(
                "Magic", 2,
                "Pokémon", 3,
                "Yu-Gi-Oh!", 1,
                "Total", 6
        );

        when(publicacionService.contarPublicacionesPorJuego()).thenReturn(mockStats);

        mockMvc.perform(get("/api/estadisticas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Magic").value(2))
                .andExpect(jsonPath("$.Pokémon").value(3))
                .andExpect(jsonPath("$.Yu-Gi-Oh!").value(1))
                .andExpect(jsonPath("$.Total").value(6));

    }

    @Disabled
    void testObtenerEstadisticasConRolAdmin() throws Exception {
        String token = "Bearer testdetoken";
        Claims claims = mock(Claims.class);

        when(claims.getSubject()).thenReturn("1"); // id del admin
        when(jwtUtil.validateToken("testdetoken")).thenReturn(claims);

        UsuarioResponseDto adminResponseDTO = adminAUsuarioResponseDTO(admin);

        when(usuarioService.buscarUsuarioPorId("1")).thenReturn(adminResponseDTO);

        Map<String, Integer> mockStats = Map.of(
                "Magic", 2,
                "Pokémon", 3,
                "Yu-Gi-Oh!", 1,
                "Total", 6
        );

        when(publicacionService.contarPublicacionesPorJuego()).thenReturn(mockStats);

        mockMvc.perform(get("/api/estadisticas")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Magic").value(2))
                .andExpect(jsonPath("$.Pokémon").value(3))
                .andExpect(jsonPath("$.Yu-Gi-Oh!").value(1))
                .andExpect(jsonPath("$.Total").value(6));
    }

    @Disabled
    void testObtenerEstadisticas_UsuarioNoAdmin_LanzaExcepcion() throws Exception {
        String token = "Bearer testdetoken";
        Claims claims = mock(Claims.class);

        when(claims.getSubject()).thenReturn("2"); // id del usuario
        when(jwtUtil.validateToken("testdetoken")).thenReturn(claims);

        UsuarioResponseDto usuarioResponseDTO = usuarioAUsuarioResponseDTO(usuario);

        when(usuarioService.buscarUsuarioPorId("2")).thenReturn(usuarioResponseDTO);

        mockMvc.perform(get("/api/estadisticas")
                        .header("Authorization", token))
                .andExpect(status().isForbidden());
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

}
