package com.example.cartasIntercambio.controller;

import com.example.cartasIntercambio.dto.*;
import com.example.cartasIntercambio.exception.PublicacionNoEncontradaException;
import com.example.cartasIntercambio.jwt.JwtUtil;
import com.example.cartasIntercambio.model.Mercado.EstadoOferta;
import com.example.cartasIntercambio.model.Mercado.EstadoPublicacion;
import com.example.cartasIntercambio.model.Mercado.Oferta;
import com.example.cartasIntercambio.model.Mercado.Publicacion;
import com.example.cartasIntercambio.model.Producto_Carta.Carta;
import com.example.cartasIntercambio.model.Producto_Carta.EstadoCarta;
import com.example.cartasIntercambio.model.Usuario.Usuario;
import com.example.cartasIntercambio.repository.irepository.IOfertaRepository;
import com.example.cartasIntercambio.repository.irepository.IPublicacionRepository;
import com.example.cartasIntercambio.repository.irepository.IUsuarioRepository;
import com.example.cartasIntercambio.service.*;

import com.github.fge.jsonpatch.JsonPatch;
import io.jsonwebtoken.Claims;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(PublicacionController.class)
@AutoConfigureMockMvc(addFilters = false) // Desactiva los filtros de Spring Security dentro del test
public class PublicacionControllerTest {
    private Carta cartaPokemon1;
    private Carta cartaPokemon2;
    private Carta cartaPokemon3;
    private Usuario publicador1;
    private Usuario publicador2;
    private Usuario publicador3;
    private Usuario ofertante1;
    private Publicacion publicacion1;
    private Publicacion publicacion2;
    private Publicacion publicacion3;
    private Publicacion publicacion4;
    private Publicacion publicacion5;
    private Publicacion publicacion6;
    private Oferta oferta1;
    private Oferta oferta2;
    private Oferta oferta3;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PublicacionServiceImpl publicacionService;

    @MockBean
    private OfertaServiceImpl ofertaService;

    @MockBean
    private IUsuarioService usuarioService;

    @MockBean
    private IUsuarioRepository usuarioRepository;

    @MockBean
    private IOfertaRepository ofertaRepository;

    @MockBean
    private IPublicacionRepository publicacionRepository;

    @MockBean
    private MongoTemplate mongoTemplate;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private S3Service s3Service;

    @BeforeEach
    void init() throws ParseException {
        List<String> imagenes = new ArrayList<>();

        // usuarios
        publicador1 = this.crearUsuarioEjemplo("1", "Juan", "Perez", "juanpe80", "jperez@cartas.com", "JuanPe!2025", "1980-05-04");
        publicador2 = this.crearUsuarioEjemplo("2", "Pedro", "Gonzales", "pedro25", "pgonzales@cartas.com", "P3dr0!", "1996-08-11");
        publicador3 = this.crearUsuarioEjemplo("3", "Mora", "Rodriguez", "Morita", "mrodriguezcartas.com", "M0r1t42013", "2013-11-13");
        ofertante1 = this.crearUsuarioEjemplo("4", "Kaila", "Rodriguez", "Kailaa", "krodriguez@cartas.com", "K41l1t4!", "2000-05-23");

        // cartas ofrecidas
        cartaPokemon1 = new Carta("Pokemon", "Pikachu", EstadoCarta.NUEVO, imagenes);
        cartaPokemon2 = new Carta("Pokemon", "Charizard", EstadoCarta.MUY_BUENO, imagenes);
        cartaPokemon3 = new Carta("Pokemon", "Bulbasaur", EstadoCarta.MUY_MALO, imagenes);

        Carta cartaMagic1 = new Carta("Magic", "Black Lotus", EstadoCarta.BUENO, imagenes);
        Carta cartaMagic2 = new Carta("Magic", "Shivan Dragon", EstadoCarta.MALO, imagenes);
        Carta cartaMagic3 = new Carta("Magic", "Llanowar Elves", EstadoCarta.MUY_MALO, imagenes);

        Carta cartaYuGiOh1 = new Carta("YuGiOh", "Blue-Eyes White Dragon", EstadoCarta.MALO, imagenes);
        Carta cartaYuGiOh2 = new Carta("YuGiOh", "Dark Magician", EstadoCarta.NUEVO, imagenes);
        Carta cartaYuGiOh3 = new Carta("YuGiOh", "Mirror Force", EstadoCarta.MUY_BUENO, imagenes);

        // publicaciones
        List<Carta> cartasDeInteres1 = Arrays.asList(cartaMagic1, cartaMagic3);
        List<Carta> cartasDeInteres2 = Arrays.asList(cartaYuGiOh1, cartaYuGiOh2);
        List<Carta> cartasDeInteres3 = Arrays.asList(cartaPokemon2, cartaPokemon3);
        List<Carta> cartasDeInteres4 = Arrays.asList(cartaMagic1, cartaMagic2);
        List<Carta> cartasDeInteres5 = Arrays.asList(cartaPokemon1, cartaPokemon3);
        List<Carta> cartasDeInteres6 = Arrays.asList(cartaPokemon1, cartaPokemon2);

        publicacion1 = crearPublicacionEjemplo("1", "Intercambio de cartas Magic", cartaMagic2, BigDecimal.valueOf(50000), cartasDeInteres1, publicador1, EstadoPublicacion.ACTIVA);
        publicacion2 = crearPublicacionEjemplo("2", "Intercambio de cartas Yu-Gi-Oh!", cartaYuGiOh3, BigDecimal.valueOf(20000), cartasDeInteres2, publicador1, EstadoPublicacion.ACTIVA);
        publicacion3 = crearPublicacionEjemplo("3", "Intercambio de cartas Pokemon", cartaPokemon1, BigDecimal.valueOf(120000), cartasDeInteres3, publicador2, EstadoPublicacion.ACTIVA);
        publicacion4 = crearPublicacionEjemplo("4", "Intercambio de cartas Magic", cartaMagic3, BigDecimal.valueOf(89500), cartasDeInteres4, publicador2, EstadoPublicacion.ACTIVA);
        publicacion5 = crearPublicacionEjemplo("5", "Intercambio de cartas Pokemon", cartaPokemon2, BigDecimal.valueOf(97300), cartasDeInteres5, publicador3, EstadoPublicacion.ACTIVA);
        publicacion6 = crearPublicacionEjemplo("6", "Intercambio de cartas Pokemon", cartaPokemon3, BigDecimal.valueOf(83400), cartasDeInteres6, publicador3, EstadoPublicacion.FINALIZADA);

        // ofertas
        oferta1 = crearOfertaEjemplo("1", publicacion1.getId(), BigDecimal.valueOf(45000), cartasDeInteres1, ofertante1);
        oferta2 = crearOfertaEjemplo("2", publicacion1.getId(), BigDecimal.valueOf(50000), cartasDeInteres1, ofertante1);
        oferta3 = crearOfertaEjemplo("3", publicacion2.getId(), BigDecimal.valueOf(13400), cartasDeInteres2, ofertante1);
    }

    // ---------------------------------------- TESTS ---------------------------------------- //

    @Test
    void testListarPublicacionesSinFiltrosPrimerPagina() throws Exception {
        PublicacionDto publicacionDto1 = publicacionAPublicacionDTO(publicacion1);
        PublicacionDto publicacionDto2 = publicacionAPublicacionDTO(publicacion2);
        PublicacionDto publicacionDto3 = publicacionAPublicacionDTO(publicacion3);

        PublicacionResponse response = new PublicacionResponse();
        response.setContent(List.of(publicacionDto1, publicacionDto2, publicacionDto3)); // primera pagina
        response.setTotalPages(2);
        response.setTotalElements(4L);
        response.setPageNo(0);
        response.setPageSize(3);

        when(publicacionService.buscarPublicacionesFiltradas(
                isNull(), isNull(), isNull(), isNull(), isNull(), eq(0), eq(3)
        )).thenReturn(response);

        mockMvc.perform(get("/api/publicaciones").param("pageNo", "0").param("pageSize", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("1"))
                .andExpect(jsonPath("$.content[1].id").value("2"))
                .andExpect(jsonPath("$.content[2].id").value("3"))
                .andExpect(jsonPath("$.content[0].descripcion").value("Intercambio de cartas Magic"))
                .andExpect(jsonPath("$.content[1].descripcion").value("Intercambio de cartas Yu-Gi-Oh!"))
                .andExpect(jsonPath("$.content[2].descripcion").value("Intercambio de cartas Pokemon"))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.totalElements").value(4))
                .andExpect(jsonPath("$.pageNo").value(0))
                .andExpect(jsonPath("$.pageSize").value(3));
    }

    @Test
    void testListarPublicacionesSinFiltrosSegundaPagina() throws Exception {
        PublicacionDto publicacionDto4 = publicacionAPublicacionDTO(publicacion4);

        PublicacionResponse response = new PublicacionResponse();
        response.setContent(List.of(publicacionDto4)); // segunda pagina
        response.setTotalPages(2);
        response.setTotalElements(4L);
        response.setPageNo(1);
        response.setPageSize(3);

        when(publicacionService.buscarPublicacionesFiltradas(
                isNull(), isNull(), isNull(), isNull(), isNull(), eq(1), eq(3)
        )).thenReturn(response);

        mockMvc.perform(get("/api/publicaciones").param("pageNo", "1").param("pageSize", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value("1"))
                .andExpect(jsonPath("$.content[0].id").value("4"))
                .andExpect(jsonPath("$.content[0].descripcion").value("Intercambio de cartas Magic"))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.totalElements").value(4))
                .andExpect(jsonPath("$.pageNo").value(1))
                .andExpect(jsonPath("$.pageSize").value(3));
    }

    @Test
    void testListarPublicacionesConFiltros() throws Exception {
        // filtro por juego Pokemon, estado ACTIVA y precio max 100000: debe traer publicacion 5
        PublicacionDto publicacionDto5 = publicacionAPublicacionDTO(publicacion5); // publicacion pokemon de 97300 activa

        PublicacionResponse response = new PublicacionResponse();
        response.setContent(List.of(publicacionDto5));
        response.setTotalPages(1);
        response.setTotalElements(1L);
        response.setPageNo(0);
        response.setPageSize(6);
        response.setLastPage(true);

        when(publicacionService.buscarPublicacionesFiltradas(
                eq(null), eq("Pokemon"), eq("ACTIVA"), eq(80000.0), eq(100000.0), eq(0), eq(6)
        )).thenReturn(response);

        mockMvc.perform(get("/api/publicaciones")
                        .param("juego", "Pokemon")
                        .param("estado", "ACTIVA")
                        .param("preciomin", "80000")
                        .param("preciomax", "100000")
                        .param("pageNo", "0")
                        .param("pageSize", "6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value("5")) // id de la publicacion 5
                .andExpect(jsonPath("$.content[0].descripcion").value("Intercambio de cartas Pokemon"))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.pageNo").value(0))
                .andExpect(jsonPath("$.pageSize").value(6));
    }

    @Test
    void buscarPublicacionPorId() throws Exception {
        PublicacionDto publicacionDto = publicacionAPublicacionDTO(publicacion4);

        when(publicacionService.buscarPublicacionDTOPorId("4")).thenReturn(publicacionDto);

        mockMvc.perform(get("/api/publicaciones/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("4"))
                .andExpect(jsonPath("$.descripcion").value("Intercambio de cartas Magic"))
                .andExpect(jsonPath("$.cartaOfrecida.nombre").value("Llanowar Elves"))
                .andExpect(jsonPath("$.publicador.id").value("2"))
                .andExpect(jsonPath("$.precio").value("89500"))
                .andExpect(jsonPath("$.estado").value("ACTIVA"));
    }

    @Test
    void testBuscarPublicacionPorId_NoExiste_LanzaExcepcion() throws Exception {
        when(publicacionService.buscarPublicacionDTOPorId("10"))
                .thenThrow(new PublicacionNoEncontradaException("No existe la publicación con id: 10"));

        mockMvc.perform(get("/api/publicaciones/10")).andExpect(status().isNotFound());
    }

    @Test
    void tesCrearPublicacion() throws Exception {
        // Creo una publicacion nueva con ID 7
        List<Carta> cartasDeInteres = Arrays.asList(cartaPokemon2, cartaPokemon3);
        CartaDto cartaDto = cartaACartaDTO(cartaPokemon1);
        Publicacion publicacion = crearPublicacionEjemplo("7", "Vendo carta Pokemon", cartaPokemon1, new BigDecimal(56700), cartasDeInteres, publicador3, EstadoPublicacion.ACTIVA);
        PublicacionDto publicacionDto = publicacionAPublicacionDTO(publicacion);

        // JWT
        String token = "Bearer testdetoken";

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("2");

        when(jwtUtil.validateToken("testdetoken")).thenReturn(claims);
        when(publicacionService.guardarPublicacion(any())).thenReturn(publicacionDto);

        // Verificamos
        mockMvc.perform(post("/api/publicaciones")
                .cookie(new Cookie("jwt", "testdetoken"))
                .contentType("application/json")
                .content(asJsonString(publicacionDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("7"))
                .andExpect(jsonPath("$.descripcion").value("Vendo carta Pokemon"))
                .andExpect(jsonPath("$.cartaOfrecida.nombre").value("Pikachu"))
                .andExpect(jsonPath("$.publicador.id").value("3"));
    }

    @Test
    void testOfertarPublicacionActiva() throws Exception {
        String idPublicacion = publicacion1.getId();
        String token = "Bearer testdetoken";
        OfertaDto ofertaDto = ofertaAOfertaDTO(oferta1);

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(ofertante1.getId());
        when(jwtUtil.validateToken("testdetoken")).thenReturn(claims);
        when(publicacionService.buscarPublicacionPorId(idPublicacion)).thenReturn(publicacion1);
        when(ofertaService.crearOferta(any(), eq(publicacion1))).thenReturn(ofertaDto);

        mockMvc.perform(post("/api/publicaciones/" + idPublicacion + "/ofertas")
                        .cookie(new Cookie("jwt", "testdetoken"))
                        .contentType("application/json")
                        .content(asJsonString(ofertaDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.monto").value(45000))
                .andExpect(jsonPath("$.cartasOfrecidas[0].nombre").value("Black Lotus"))
                .andExpect(jsonPath("$.cartasOfrecidas[1].nombre").value("Llanowar Elves"));
    }

    @Test
    void testOfertarPublicacionFinalizada_LanzaExcepcion() throws Exception {
        List<Carta> cartasOfrecidas = Arrays.asList(cartaPokemon1, cartaPokemon2);
        String idPublicacion = publicacion6.getId();
        String token = "Bearer testdetoken";
        Oferta oferta = crearOfertaEjemplo("2", publicacion6.getId(), BigDecimal.valueOf(77100), cartasOfrecidas, ofertante1);
        OfertaDto ofertaDto = ofertaAOfertaDTO(oferta);

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(ofertante1.getId());
        when(jwtUtil.validateToken("testdetoken")).thenReturn(claims);

        // Simula la publicacion FINALIZADA en el servicio
        when(publicacionService.buscarPublicacionPorId(idPublicacion)).thenReturn(publicacion6);

        when(ofertaService.crearOferta(any(), eq(publicacion6)))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "La publicación ya no está activa."));

        mockMvc.perform(post("/api/publicaciones/" + idPublicacion + "/ofertas")
                .cookie(new Cookie("jwt", "testdetoken"))
                .contentType("application/json")
                .content(asJsonString(ofertaDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testBuscarOfertasDeUnaPublicacion() throws Exception {
        String idPublicacion = publicacion1.getId();
        OfertaDto ofertaDto1 = ofertaAOfertaDTO(oferta1);
        OfertaDto ofertaDto2 = ofertaAOfertaDTO(oferta2);
        PublicacionDto publicacionDto1 = publicacionAPublicacionDTO(publicacion1);

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("1");
        when(jwtUtil.validateToken("testdetoken")).thenReturn(claims);

        when(publicacionService.buscarPublicacionDTOPorId(idPublicacion)).thenReturn(publicacionDto1);
        when(ofertaService.buscarOfertasPorPublicacion(idPublicacion)).thenReturn(List.of(ofertaDto1, ofertaDto2));

        mockMvc.perform(get("/api/publicaciones/" + idPublicacion + "/ofertas")
                        .cookie(new Cookie("jwt", "testdetoken")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[0].monto").value(45000))
                .andExpect(jsonPath("$[1].monto").value(50000))
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"))
                .andExpect(jsonPath("$[1].estado").value("PENDIENTE"))
                .andExpect(jsonPath("$[0].cartasOfrecidas[0].nombre").value("Black Lotus"))
                .andExpect(jsonPath("$[0].cartasOfrecidas[1].nombre").value("Llanowar Elves"))
                .andExpect(jsonPath("$[1].cartasOfrecidas[0].nombre").value("Black Lotus"))
                .andExpect(jsonPath("$[1].cartasOfrecidas[1].nombre").value("Llanowar Elves"));
    }

    @Test
    void testBuscarOfertasDeUnaPublicacionNoExiste_LanzaExcepcion() throws Exception {
        String idPublicacion = "10";

        // Mock del JWT
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("2");
        when(jwtUtil.validateToken("testdetoken")).thenReturn(claims);

        // no se llega a ejecutar ofertaService.buscarOfertasPorPublicacion
        when(publicacionService.buscarPublicacionDTOPorId(idPublicacion))
                .thenThrow(new PublicacionNoEncontradaException("No existe la publicación con id: " + idPublicacion));

        mockMvc.perform(get("/api/publicaciones/" + idPublicacion + "/ofertas")
                .cookie(new Cookie("jwt", "testdetoken")))
                .andExpect(status().isNotFound());
    }

    @Test
    void testBuscarOfertaPorId() throws Exception {
        String idOferta = oferta1.getId();
        OfertaDto ofertaDto = ofertaAOfertaDTO(oferta1);
        PublicacionDto publicacionDto1 = publicacionAPublicacionDTO(publicacion1);

        // Mock de JWT y de la publicacion asociada a la oferta 1
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(oferta1.getOfertante().getId());
        when(jwtUtil.validateToken("testdetoken")).thenReturn(claims);

        when(ofertaService.buscarOfertaDto(idOferta)).thenReturn(ofertaDto);
        when(publicacionService.buscarPublicacionDTOPorId(ofertaDto.getIdPublicacion()))
                .thenReturn(publicacionDto1);

        mockMvc.perform(get("/api/publicaciones/ofertas/1")
                .cookie(new Cookie("jwt", "testdetoken")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idOferta))
                .andExpect(jsonPath("$.monto").value(45000))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.cartasOfrecidas[0].nombre").value("Black Lotus"))
                .andExpect(jsonPath("$.cartasOfrecidas[1].nombre").value("Llanowar Elves"));
    }

    @Test
    void testBuscarOfertaPorId_NoExiste_LanzaExcepcion() throws Exception {
        String idOferta = "10";

        // Mock del JWT
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("2");
        when(jwtUtil.validateToken("testdetoken")).thenReturn(claims);

        // Mock de la excepcion
        when(ofertaService.buscarOfertaDto(idOferta))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la oferta con id: " + idOferta));

        mockMvc.perform(get("/api/publicaciones/ofertas/" + idOferta)
                        .cookie(new Cookie("jwt", "testdetoken")))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAceptarOferta() throws Exception {
        String token = "Bearer testdetoken";
        String idPublicador = publicacion1.getPublicador().getId();
        String idPublicacion = publicacion1.getId();
        String idOferta = oferta1.getId();

        // Simular token valido
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(idPublicador);
        when(jwtUtil.validateToken("testdetoken")).thenReturn(claims);

        // Mockeamos oferta y publicacion
        when(ofertaService.buscarOfertaPorId(idOferta)).thenReturn(oferta1);
        when(publicacionService.buscarPublicacionPorId(idPublicacion)).thenReturn(publicacion1);

        // Simulamos el patch
        Oferta ofertaAceptada = crearOfertaEjemplo(oferta1.getId(), oferta1.getIdPublicacion(), oferta1.getMonto(), oferta1.getCartasOfrecidas(), oferta1.getOfertante());
        ofertaAceptada.setEstado(EstadoOferta.ACEPTADO);

        JsonPatch patch = JsonPatch.fromJson(
                new ObjectMapper().readTree("[{ \"op\": \"replace\", \"path\": \"/estado\", \"value\": \"ACEPTADO\" }]")
        );

        // Mock patchOferta y guardarOferta
        doNothing().when(ofertaService).guardarOferta(any());
        doNothing().when(ofertaService).rechazarOtrasOfertas(idOferta, idPublicacion);
        doNothing().when(publicacionService).finalizarPublicacion(idPublicacion, publicacion1.getVersion());

        mockMvc.perform(patch("/api/publicaciones/ofertas/" + idOferta)
                        .cookie(new Cookie("jwt", "testdetoken"))
                        .contentType("application/json-patch+json")
                        .content("[{ \"op\": \"replace\", \"path\": \"/estado\", \"value\": \"ACEPTADO\" }]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ACEPTADO"));
    }

    @Test
    void testResponderOferta_UsuarioNoAutorizado() throws Exception {
        String token = "Bearer testdetoken";
        String idPublicacion = publicacion1.getId();
        String idOferta = oferta1.getId();

        // Simular token valido
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("2"); // ID de otro usuario que no es el publicador
        when(jwtUtil.validateToken("testdetoken")).thenReturn(claims);

        // Mockeamos oferta y publicacion
        when(ofertaService.buscarOfertaPorId(idOferta)).thenReturn(oferta1);
        when(publicacionService.buscarPublicacionPorId(idPublicacion)).thenReturn(publicacion1);

        mockMvc.perform(patch("/api/publicaciones/ofertas/" + idOferta)
                        .cookie(new Cookie("jwt", "testdetoken"))
                        .contentType("application/json-patch+json")
                        .content("[{ \"op\": \"replace\", \"path\": \"/estado\", \"value\": \"ACEPTADO\" }]"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testAlAceptarOfertaRechazaElRestoYFinalizaPublicacion() throws Exception {
        String token = "Bearer testdetoken";
        String idPublicador = publicacion1.getPublicador().getId();
        String idPublicacion = publicacion1.getId();
        String idOferta = oferta1.getId();

        // Simular token valido
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(idPublicador);
        when(jwtUtil.validateToken("testdetoken")).thenReturn(claims);

        // Mock oferta y publicacion
        when(ofertaService.buscarOfertaPorId(idOferta)).thenReturn(oferta1);
        when(publicacionService.buscarPublicacionPorId(idPublicacion)).thenReturn(publicacion1);

        // Simulamos el patch
        Oferta ofertaAceptada = crearOfertaEjemplo(oferta1.getId(), oferta1.getIdPublicacion(), oferta1.getMonto(), oferta1.getCartasOfrecidas(), oferta1.getOfertante());
        ofertaAceptada.setEstado(EstadoOferta.ACEPTADO);

        String patchJson = "[{ \"op\": \"replace\", \"path\": \"/estado\", \"value\": \"ACEPTADO\" }]";

        doNothing().when(ofertaService).guardarOferta(any());
        doNothing().when(ofertaService).rechazarOtrasOfertas(idOferta, idPublicacion);
        doNothing().when(publicacionService).finalizarPublicacion(idPublicacion, publicacion1.getVersion());

        mockMvc.perform(patch("/api/publicaciones/ofertas/" + idOferta)
                        .cookie(new Cookie("jwt", "testdetoken"))
                        .contentType("application/json-patch+json")
                        .content(patchJson))
                .andExpect(status().isOk());

        verify(ofertaService).rechazarOtrasOfertas(idOferta, idPublicacion);
        verify(publicacionService).finalizarPublicacion(idPublicacion, publicacion1.getVersion());
    }

    @Test
    void testRechazarOferta() throws Exception {
        String token = "Bearer testdetoken";
        String idPublicador = publicacion1.getPublicador().getId();
        String idPublicacion = publicacion1.getId();
        String idOferta = oferta1.getId();

        // Simular token valido
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(idPublicador);
        when(jwtUtil.validateToken("testdetoken")).thenReturn(claims);

        // Mock oferta y publicacion
        when(ofertaService.buscarOfertaPorId(idOferta)).thenReturn(oferta1);
        when(publicacionService.buscarPublicacionPorId(idPublicacion)).thenReturn(publicacion1);

        doNothing().when(ofertaService).guardarOferta(any());

        String patchJson = "[{ \"op\": \"replace\", \"path\": \"/estado\", \"value\": \"RECHAZADO\" }]";

        mockMvc.perform(patch("/api/publicaciones/ofertas/" + idOferta)
                        .cookie(new Cookie("jwt", "testdetoken"))
                        .contentType("application/json-patch+json")
                        .content(patchJson))
                .andExpect(status().isOk());

        verify(ofertaService, never()).rechazarOtrasOfertas(any(), any());
        verify(publicacionService, never()).finalizarPublicacion(any(), any());
    }

    @Test
    void testListarPublicacionesPorUsuario() throws Exception {
        // Publicaciones del usuario publicador1 con ID 1
        PublicacionDto publicacionDto1 = publicacionAPublicacionDTO(publicacion1);
        PublicacionDto publicacionDto2 = publicacionAPublicacionDTO(publicacion2);

        PublicacionResponse response = new PublicacionResponse();
        response.setContent(List.of(publicacionDto1, publicacionDto2));
        response.setTotalPages(1);
        response.setTotalElements(2L);
        response.setPageNo(0);
        response.setPageSize(6);
        response.setLastPage(true);

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("1");
        when(jwtUtil.validateToken("testdetoken")).thenReturn(claims);

        when(publicacionService.buscarPublicacionesPorUsuario("1", 0, 6)).thenReturn(response);

        mockMvc.perform(get("/api/publicaciones/usuario/1")
                        .param("pageNo", "0")
                        .param("pageSize", "6")
                        .cookie(new Cookie("jwt", "testdetoken")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value("1"))
                .andExpect(jsonPath("$.content[0].publicador.id").value("1"))
                .andExpect(jsonPath("$.content[1].id").value("2"))
                .andExpect(jsonPath("$.content[1].publicador.id").value("1"))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.pageNo").value(0))
                .andExpect(jsonPath("$.pageSize").value(6));
    }

    @Test
    void testListarPublicacionesPorUsuario_PaginaVacia() throws Exception {
        //  Simulamos que la pagina 1 esta vacia
        PublicacionResponse response = new PublicacionResponse();
        response.setContent(List.of());
        response.setTotalPages(1); // solo hay una página válida (la 0)
        response.setTotalElements(2L);
        response.setPageNo(1);
        response.setPageSize(2);
        response.setLastPage(true);

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("1");
        when(jwtUtil.validateToken("testdetoken")).thenReturn(claims);

        when(publicacionService.buscarPublicacionesPorUsuario("1", 1, 2)).thenReturn(response);

        mockMvc.perform(get("/api/publicaciones/usuario/1")
                        .param("pageNo", "1")
                        .param("pageSize", "2")
                        .cookie(new Cookie("jwt", "testdetoken")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.pageNo").value(1))
                .andExpect(jsonPath("$.pageSize").value(2))
                .andExpect(jsonPath("$.lastPage").value(true));
    }

    @Test
    void testListarOfertasRecibidasParaUnUsuario() throws Exception {
        // Publicador1 tiene dos publicaciones: publicacion1 y publicacion2
        PublicacionDto publicacionDto1 = publicacionAPublicacionDTO(publicacion1);
        PublicacionDto publicacionDto2 = publicacionAPublicacionDTO(publicacion2);

        // Las ofertas 1 y 2 corresponden a la publicacion 1
        // La oferta 3 corresponde a la publicacion 2
        OfertaDto ofertaDto1 = ofertaAOfertaDTO(oferta1);
        OfertaDto ofertaDto2 = ofertaAOfertaDTO(oferta2);
        OfertaDto ofertaDto3 = ofertaAOfertaDTO(oferta3);

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("1");
        when(jwtUtil.validateToken("testdetoken")).thenReturn(claims);

        // Mock servicios
        when(publicacionService.buscarPublicacionesPorUsuario("1"))
                .thenReturn(List.of(publicacionDto1, publicacionDto2));

        when(ofertaService.buscarOfertasPorPublicacion(publicacion1.getId()))
                .thenReturn(List.of(ofertaDto1, ofertaDto2));

        when(ofertaService.buscarOfertasPorPublicacion(publicacion2.getId()))
                .thenReturn(List.of(ofertaDto3));

        mockMvc.perform(get("/api/publicaciones/usuario/1/ofertas/recibidas")
                        .cookie(new Cookie("jwt", "testdetoken")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[2].id").value("3"));
    }

    @Test
    void testListarOfertasRealizadasPorUnUsuario() throws Exception {
        // El ofertante 1 con ID 4 realizo las ofertas 1 y 2
        OfertaDto ofertaDto1 = ofertaAOfertaDTO(oferta1);
        OfertaDto ofertaDto2 = ofertaAOfertaDTO(oferta2);

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("4"); // id del ofertante
        when(jwtUtil.validateToken("testdetoken")).thenReturn(claims);

        when(ofertaService.buscarOfertasRealizadas("4"))
                .thenReturn(List.of(ofertaDto1, ofertaDto2));

        mockMvc.perform(get("/api/publicaciones/usuario/4/ofertas/realizadas")
                .cookie(new Cookie("jwt", "testdetoken")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[0].ofertante.id").value("4"))
                .andExpect(jsonPath("$[1].ofertante.id").value("4"));
    }

    @Test
    void testListarOfertasRealizadasPorUnUsuario_SinOfertas() throws Exception {
        // El usuario con ID 3 es elpublicador3 el cual no realizo ofertas
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("3");
        when(jwtUtil.validateToken("testdetoken")).thenReturn(claims);

        when(ofertaService.buscarOfertasRealizadas("3")).thenReturn(List.of());

        mockMvc.perform(get("/api/publicaciones/usuario/3/ofertas/realizadas")
                        .cookie(new Cookie("jwt", "testdetoken")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testCrearPublicacionMultipart() throws Exception {
        // El usuario sube una publicación con una imagen para la carta principal y al menos una para cada carta de interés
        String token = "Bearer testdetoken";
        String idUsuario = "1"; // publicador1

        // Simulamos los claims del JWT
        Claims claims = mock(Claims.class);

        when(claims.getSubject()).thenReturn(idUsuario);
        when(jwtUtil.validateToken("testdetoken")).thenReturn(claims);

        // Publicador
        UsuarioResponseDto publicadorResponseDTO = usuarioAUsuarioResponseDTO(publicador1);

        when(usuarioService.buscarUsuarioPorId(idUsuario)).thenReturn(publicadorResponseDTO);

        // Publicacion 1 con cartaMagic2 de 50000 y con cartasDeInteres1
        PublicacionDto publicacionDto1 = publicacionAPublicacionDTO(publicacion1);

        // Simulamos las imagenes
        MockMultipartFile cartaPublicadaImagen = new MockMultipartFile("publicacionImagenes", "ShivanDragon.png", "image/png", "dummy".getBytes());
        MockMultipartFile cartaInteresImagen1 = new MockMultipartFile("cartaInteres[0]", "BlackLotus.png", "image/png", "img1".getBytes());
        MockMultipartFile cartaInteresImagen3 = new MockMultipartFile("cartaInteres[1]", "LlanowarElves.png", "image/png", "img2".getBytes());

        // Simulamos la subida a AWS S3
        when(s3Service.uploadFile(any())).thenReturn("https://mock-bucket.s3.amazonaws.com/imagen.png");

        // Simular guardado final
        when(publicacionService.guardarPublicacion(any())).thenReturn(publicacionDto1);

        MockPart publicacionPart = new MockPart("publicacion", new ObjectMapper().writeValueAsBytes(publicacionDto1));
        publicacionPart.getHeaders().setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        mockMvc.perform(multipart("/api/publicaciones")
                        .file(cartaPublicadaImagen)
                        .file(cartaInteresImagen1)
                        .file(cartaInteresImagen3)
                        .part(publicacionPart)
                        .cookie(new Cookie("jwt", "testdetoken"))
                        .contentType("multipart/form-data"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.descripcion").value("Intercambio de cartas Magic"))
                .andExpect(jsonPath("$.cartaOfrecida.nombre").value("Shivan Dragon"))
                .andExpect(jsonPath("$.cartasInteres.length()").value(2))
                .andExpect(jsonPath("$.cartasInteres[0].nombre").value("Black Lotus"))
                .andExpect(jsonPath("$.cartasInteres[1].nombre").value("Llanowar Elves"));
    }

    // -------------------------------------------------------------------------------------- //
    // --------------------------------- Metodos Auxiliares --------------------------------- //

    private Publicacion crearPublicacionEjemplo(String id, String descripcion, Carta cartaOfrecida, BigDecimal precio, List<Carta> cartasInteres, Usuario publicador, EstadoPublicacion estado) {
        Publicacion publicacion = new Publicacion();

        publicacion.setId(id);
        publicacion.setFecha(new Date());
        publicacion.setDescripcion(descripcion);
        publicacion.setCartaOfrecida(cartaOfrecida);
        publicacion.setPrecio(precio);
        publicacion.setCartasInteres(cartasInteres);
        publicacion.setPublicador(publicador);
        publicacion.setEstado(estado);

        return publicacion;
    }

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

    private Oferta crearOfertaEjemplo(String id, String idPublicacion, BigDecimal monto, List<Carta> cartasOfrecidas, Usuario ofertante) {
        Oferta oferta = new Oferta();

        oferta.setId(id);
        oferta.setIdPublicacion(idPublicacion);
        oferta.setFecha(new Date());
        oferta.setMonto(monto);
        oferta.setCartasOfrecidas(cartasOfrecidas);
        oferta.setOfertante(ofertante);
        oferta.setEstado(EstadoOferta.PENDIENTE);

        return oferta;
    }

    // Creacion DTOs

    public PublicacionDto publicacionAPublicacionDTO(Publicacion publicacion) {

        PublicacionDto publicacionDTO = new PublicacionDto(
                publicacion.getId(),
                publicacion.getFecha(),
                publicacion.getDescripcion(),
                cartaACartaDTO(publicacion.getCartaOfrecida()),
                publicacion.getPrecio(),
                cartasACartasDTOs(publicacion.getCartasInteres()),
                publicacion.getPublicador(),
                publicacion.getEstado().toString(),
                publicacion.getVersion()
        );

        return publicacionDTO;
    }

    public OfertaDto ofertaAOfertaDTO(Oferta oferta) {

        OfertaDto ofertaDTO = new OfertaDto(
                oferta.getFecha(),
                oferta.getIdPublicacion(),
                oferta.getMonto(),
                oferta.getCartasOfrecidas(),
                oferta.getOfertante(),
                "PENDIENTE"
        );

        ofertaDTO.setId(oferta.getId());

        return ofertaDTO;
    }

    private CartaDto cartaACartaDTO(Carta carta) {
        return new CartaDto(
                carta.getJuego(),
                carta.getNombre(),
                carta.getEstado().toString(),
                carta.getImagenes()
        );
    }

    private List<CartaDto> cartasACartasDTOs(List<Carta> cartas) {
        List<CartaDto> cartasDTOs = new ArrayList<>();

        for (Carta carta : cartas) cartasDTOs.add(cartaACartaDTO(carta));
        return cartasDTOs;
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

    private static String asJsonString(final Object obj) {
        try {
            //return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(obj);
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}