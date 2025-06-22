package com.example.cartasIntercambio.service;

import com.example.cartasIntercambio.dto.CartaDto;
import com.example.cartasIntercambio.dto.PublicacionDto;
import com.example.cartasIntercambio.dto.PublicacionResponse;
import com.example.cartasIntercambio.exception.PublicacionNoEncontradaException;
import com.example.cartasIntercambio.model.Mercado.EstadoPublicacion;
import com.example.cartasIntercambio.model.Mercado.Publicacion;
import com.example.cartasIntercambio.model.Producto_Carta.Carta;
import com.example.cartasIntercambio.model.Producto_Carta.EstadoCarta;
import com.example.cartasIntercambio.model.Usuario.Usuario;
import com.example.cartasIntercambio.repository.irepository.IPublicacionRepository;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class PublicacionServiceImplTest {
    private Carta cartaPokemon1;
    private Carta cartaPokemon2;
    private Carta cartaPokemon3;
    private Usuario publicador1;
    private Usuario publicador2;
    private Publicacion publicacion1;
    private Publicacion publicacion2;
    private Publicacion publicacion3;
    private Publicacion publicacion4;

    @Mock
    private IPublicacionRepository publicacionRepository;

    @InjectMocks
    private PublicacionServiceImpl publicacionService;

    @Mock
    MongoTemplate mongoTemplate;

    @BeforeEach
    void init() throws ParseException {
        List<String> imagenes = new ArrayList<>();

        // usuarios
        publicador1 = this.crearUsuarioEjemplo("3", "Juan", "Perez", "juanpe80", "jperez@cartas.com", "JuanPe!2025", "1980-05-04");
        publicador2 = this.crearUsuarioEjemplo("4", "Pedro", "Gonzales", "pedro25", "pgonzales@cartas.com", "P3dr0!", "1996-08-11");

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

        publicacion1 = crearPublicacionEjemplo("1", "Intercambio de cartas Magic", cartaMagic2, BigDecimal.valueOf(50000), cartasDeInteres1, publicador1);
        publicacion2 = crearPublicacionEjemplo("2", "Intercambio de cartas Yu-Gi-Oh!", cartaYuGiOh3, BigDecimal.valueOf(20000), cartasDeInteres2, publicador1);
        publicacion3 = crearPublicacionEjemplo("3", "Intercambio de cartas Pokemon", cartaPokemon1, BigDecimal.valueOf(120000), cartasDeInteres3, publicador2);
        publicacion4 = crearPublicacionEjemplo("4", "Venta de carta Pikachu NUEVA", cartaPokemon1, BigDecimal.valueOf(88300), cartasDeInteres3, publicador1);
    }

    // ---------------------------------------- TESTS ---------------------------------------- //

    @Test
    void testCrearPublicacion() {
        Date fechaHoy = new Date();
        BigDecimal precio = BigDecimal.valueOf(100000);

        List<CartaDto> cartasDeInteresDto = cartasToDtos(Arrays.asList(cartaPokemon2, cartaPokemon3));
        PublicacionDto publicacionDto = new PublicacionDto();
        publicacionDto.setFecha(fechaHoy);
        publicacionDto.setDescripcion("Intercambio de cartas Pokemon");
        publicacionDto.setCartaOfrecida(cartaToDto(cartaPokemon1));
        publicacionDto.setPrecio(precio);
        publicacionDto.setCartasInteres(cartasDeInteresDto);
        publicacionDto.setPublicador(publicador1);
        publicacionDto.setEstado(EstadoPublicacion.ACTIVA.toString());

        Publicacion publicacionMock = new Publicacion(
                null, fechaHoy, "Intercambio de cartas Pokemon", cartaPokemon1, precio,
                Arrays.asList(cartaPokemon2, cartaPokemon3), publicador1, EstadoPublicacion.ACTIVA, null
        );
        when(publicacionRepository.save(any(Publicacion.class))).thenReturn(publicacionMock);

        publicacionService.guardarPublicacion(publicacionDto);

        ArgumentCaptor<Publicacion> captor = ArgumentCaptor.forClass(Publicacion.class);
        verify(publicacionRepository, times(1)).save(captor.capture());

        Publicacion publicacionGuardada = captor.getValue();

        assertEquals(fechaHoy, publicacionGuardada.getFecha());
        assertEquals("Intercambio de cartas Pokemon", publicacionGuardada.getDescripcion());
        assertEquals("Pikachu", publicacionGuardada.getCartaOfrecida().getNombre());
        assertEquals(precio, publicacionGuardada.getPrecio());
        assertEquals(publicador1, publicacionGuardada.getPublicador());
        assertEquals(EstadoPublicacion.ACTIVA, publicacionGuardada.getEstado());

        List<String> nombresCI = publicacionGuardada.getCartasInteres()
                .stream().map(Carta::getNombre).collect(Collectors.toList());
        assertTrue(nombresCI.contains("Charizard"));
        assertTrue(nombresCI.contains("Bulbasaur"));
    }

    @Test
    void testBuscarPublicacionPorId() {
        when(publicacionRepository.findById("2")).thenReturn(Optional.of(publicacion2));

        PublicacionDto publicacionDtoEncontrada = publicacionService.buscarPublicacionDTOPorId("2");

        assertNotNull(publicacionDtoEncontrada);
        assertEquals("2", publicacionDtoEncontrada.getId());
        assertEquals("Intercambio de cartas Yu-Gi-Oh!", publicacionDtoEncontrada.getDescripcion());
        assertEquals("Mirror Force", publicacionDtoEncontrada.getCartaOfrecida().getNombre());
        assertEquals("Blue-Eyes White Dragon", publicacionDtoEncontrada.getCartasInteres().get(0).getNombre());
        assertEquals("Dark Magician", publicacionDtoEncontrada.getCartasInteres().get(1).getNombre());
        assertEquals("ACTIVA", publicacionDtoEncontrada.getEstado());
    }

    @Test
    void testBuscarPublicacionPorId_NoExiste_LanzaExcepcion() {
        when(publicacionRepository.findById("1")).thenReturn(Optional.empty());

        PublicacionNoEncontradaException exception = assertThrows(PublicacionNoEncontradaException.class, () -> {
            publicacionService.buscarPublicacionDTOPorId("1");
        });

        assertTrue(exception.getMessage().contains("No existe la publicación con id: 1"));
    }

    @Test
    void testListarPublicaciones() {
        when(publicacionRepository.findAll()).thenReturn(Arrays.asList(publicacion1, publicacion2, publicacion3));

        List<PublicacionDto> publicacionesDTOs = publicacionService.listarPublicaciones();

        assertNotNull(publicacionesDTOs);
        assertEquals(3, publicacionesDTOs.size());

        PublicacionDto publicacionDTO1 = publicacionesDTOs.get(0);
        assertEquals("1", publicacionDTO1.getId());
        assertEquals("Intercambio de cartas Magic", publicacionDTO1.getDescripcion());
        assertEquals("Shivan Dragon", publicacionDTO1.getCartaOfrecida().getNombre());
        assertEquals("Black Lotus", publicacionDTO1.getCartasInteres().get(0).getNombre());
        assertEquals("ACTIVA", publicacionDTO1.getEstado());

        PublicacionDto publicacionDTO2 = publicacionesDTOs.get(1);
        assertEquals("2", publicacionDTO2.getId());
        assertEquals("Intercambio de cartas Yu-Gi-Oh!", publicacionDTO2.getDescripcion());
        assertEquals("Mirror Force", publicacionDTO2.getCartaOfrecida().getNombre());
        assertEquals("Blue-Eyes White Dragon", publicacionDTO2.getCartasInteres().get(0).getNombre());
        assertEquals("ACTIVA", publicacionDTO2.getEstado());

        PublicacionDto publicacionDTO3 = publicacionesDTOs.get(2);
        assertEquals("3", publicacionDTO3.getId());
        assertEquals("Intercambio de cartas Pokemon", publicacionDTO3.getDescripcion());
        assertEquals("Pikachu", publicacionDTO3.getCartaOfrecida().getNombre());
        assertEquals("Bulbasaur", publicacionDTO3.getCartasInteres().get(1).getNombre());
        assertEquals("ACTIVA", publicacionDTO3.getEstado());

        verify(publicacionRepository, times(1)).findAll();
    }

    @Test
    void testBuscarPublicacionPorIDUsuario() {
        when(publicacionRepository.findByPublicadorId(publicador1.getId())).thenReturn(Arrays.asList(publicacion1, publicacion2));

        List<PublicacionDto> publicacionesDTOs = publicacionService.buscarPublicacionesPorUsuario(publicador1.getId());

        assertNotNull(publicacionesDTOs);
        assertEquals(2, publicacionesDTOs.size());

        PublicacionDto publicacionDTO1 = publicacionesDTOs.get(0);
        assertEquals("1", publicacionDTO1.getId());
        assertEquals("Intercambio de cartas Magic", publicacionDTO1.getDescripcion());
        assertEquals("Shivan Dragon", publicacionDTO1.getCartaOfrecida().getNombre());
        assertEquals("Black Lotus", publicacionDTO1.getCartasInteres().get(0).getNombre());
        assertEquals("ACTIVA", publicacionDTO1.getEstado());

        PublicacionDto publicacionDTO2 = publicacionesDTOs.get(1);
        assertEquals("2", publicacionDTO2.getId());
        assertEquals("Intercambio de cartas Yu-Gi-Oh!", publicacionDTO2.getDescripcion());
        assertEquals("Mirror Force", publicacionDTO2.getCartaOfrecida().getNombre());
        assertEquals("Blue-Eyes White Dragon", publicacionDTO2.getCartasInteres().get(0).getNombre());
        assertEquals("ACTIVA", publicacionDTO2.getEstado());

        verify(publicacionRepository, times(1)).findByPublicadorId(publicador1.getId());
    }

    @Test
    void testBuscarPublicacionesFiltradas() {
        String nombreCarta = "Pikachu";
        String juego = "Pokemon";
        String estado = "ACTIVA";
        Double precioMinimo = 80000.0;
        Double precioMaximo = 100000.0;
        int numeroPagina = 0;
        int tamanioPagina = 2;

        Pageable pageable = PageRequest.of(numeroPagina, tamanioPagina);

        List<Publicacion> publicaciones = List.of(publicacion4); // la
        Page<Publicacion> pagina = new PageImpl<>(publicaciones, pageable, publicaciones.size());

        when(publicacionRepository.findByFiltros(nombreCarta, juego, estado, precioMinimo, precioMaximo, pageable))
                .thenReturn(pagina);

        PublicacionResponse response = publicacionService.buscarPublicacionesFiltradas(
                nombreCarta, juego, estado, precioMinimo, precioMaximo, numeroPagina, tamanioPagina);

        BigDecimal precioPublicacion4 = response.getContent().get(0).getPrecio();

        assertTrue(precioPublicacion4.compareTo(new BigDecimal(precioMinimo)) >= 0);
        assertTrue(precioPublicacion4.compareTo(new BigDecimal(precioMaximo)) <= 0);
        assertEquals(1, response.getContent().size());
        assertEquals(0, response.getPageNo());
        assertEquals(2, response.getPageSize());
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getTotalPages());
        assertTrue(response.isLastPage());

        verify(publicacionRepository).findByFiltros(nombreCarta, juego, estado, precioMinimo, precioMaximo, pageable);
    }

    @Test
    void testBuscarPublicacionesFiltradas_ConFiltrosNulos() {
        Pageable pageable = PageRequest.of(0, 6);
        List<Publicacion> publicaciones = List.of();
        Page<Publicacion> pagina = new PageImpl<>(publicaciones, pageable, 0);

        when(publicacionRepository.findByFiltros(null, null, null, null, null, pageable))
                .thenReturn(pagina);

        PublicacionResponse response = publicacionService.buscarPublicacionesFiltradas(
                null, null, null, null, null, 0, 6);

        assertEquals(0, response.getContent().size());
        assertEquals(0, response.getTotalElements());
        assertEquals(0, response.getTotalPages());
        assertEquals(0, response.getPageNo());
        assertEquals(6, response.getPageSize());
        assertTrue(response.isLastPage());

        verify(publicacionRepository).findByFiltros(null, null, null, null, null, pageable);
    }

    @Test
    void testContarPublicacionPorCadaJuego() {
        // Simulamos que la bd nos devuelve estos resultados
        PublicacionServiceImpl.Stat statMagic = new PublicacionServiceImpl.Stat("Magic", 3);
        PublicacionServiceImpl.Stat statPokemon = new PublicacionServiceImpl.Stat("Pokémon", 2);
        PublicacionServiceImpl.Stat statYuGiOh = new PublicacionServiceImpl.Stat("Yu-Gi-Oh!", 5);
        List<PublicacionServiceImpl.Stat> stats = List.of(statMagic, statPokemon, statYuGiOh);

        // Simulamos el resultado de aggregate()
        AggregationResults<PublicacionServiceImpl.Stat> resultados = new AggregationResults<>(stats, new Document());
        when(mongoTemplate.aggregate(any(Aggregation.class), eq("Publicacion"), eq(PublicacionServiceImpl.Stat.class)))
                .thenReturn(resultados);

        Map<String, Integer> resultado = publicacionService.contarPublicacionesPorJuego();

        assertEquals(3, resultado.get("Magic"));
        assertEquals(2, resultado.get("Pokémon"));
        assertEquals(5, resultado.get("Yu-Gi-Oh!"));
        assertEquals(10, resultado.get("Total"));
    }


    @Test
    void testFinalizarPublicacion() {
        Long version = 1L;
        String idPublicacion = publicacion1.getId();

        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class), eq(Publicacion.class)))
                .thenReturn(publicacion1);

        assertDoesNotThrow(() -> publicacionService.finalizarPublicacion(idPublicacion, version));

        ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
        ArgumentCaptor<Update> updateCaptor = ArgumentCaptor.forClass(Update.class);

        verify(mongoTemplate).findAndModify(queryCaptor.capture(), updateCaptor.capture(), eq(Publicacion.class));

        Query usedQuery = queryCaptor.getValue();
        Update usedUpdate = updateCaptor.getValue();

        assertTrue(usedQuery.getQueryObject().toString().contains("_id"));
        assertTrue(usedQuery.getQueryObject().toString().contains("version"));

        assertEquals(EstadoPublicacion.FINALIZADA,
                usedUpdate.getUpdateObject().get("$set", Document.class).get("estado"));
    }

    @Test
    void testFinalizarPublicacion_YaFueModificadaPorOtroUsuario_LanzaExcepcion() {
        Long version = 1L;
        String idPublicacion = publicacion1.getId();

        // Simulamos que no se modifico nada
        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class), eq(Publicacion.class)))
                .thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> publicacionService.finalizarPublicacion(idPublicacion, version));

        assertEquals("La publicación fue modificada por otro usuario (o ya no existe).", exception.getMessage());
    }

    @Test
    void testBuscarPublicacionesDeUnUsuario() {
        String idUsuario = "1";

        when(publicacionRepository.findByPublicadorId(idUsuario)).thenReturn(List.of(publicacion1, publicacion2));

        List<PublicacionDto> publicacionesEncontradas = publicacionService.buscarPublicacionesPorUsuario(idUsuario);

        assertEquals(2, publicacionesEncontradas.size());
        assertEquals("1", publicacionesEncontradas.get(0).getId());
        assertEquals("2", publicacionesEncontradas.get(1).getId());
    }

    @Test
    void testBuscarPublicacionesDeUnUsuarioPaginado() {
        String idUsuario = "1";
        int numeroPagina = 0;
        int tamanioPagina = 2;

        Page<Publicacion> pageMock = new PageImpl<>(
                List.of(publicacion1, publicacion2),
                PageRequest.of(numeroPagina, tamanioPagina),
                2
        );

        when(publicacionRepository.findByPublicadorId(eq(idUsuario), any(Pageable.class)))
                .thenReturn(pageMock);

        PublicacionResponse response = publicacionService.buscarPublicacionesPorUsuario(idUsuario, numeroPagina, tamanioPagina);

        assertEquals(2, response.getContent().size());
        assertEquals("1", response.getContent().get(0).getId());
        assertEquals("2", response.getContent().get(1).getId());
        assertEquals(0, response.getPageNo());
        assertEquals(2, response.getPageSize());
        assertEquals(1, response.getTotalPages());
        assertEquals(2, response.getTotalElements());
        assertTrue(response.isLastPage());
    }

    // ------- Métodos auxiliares DTO -------

    private CartaDto cartaToDto(Carta c) {
        return new CartaDto(
                c.getJuego(),
                c.getNombre(),
                c.getEstado().toString(),
                c.getImagenes()
        );
    }

    private List<CartaDto> cartasToDtos(List<Carta> cs) {
        List<CartaDto> out = new ArrayList<>();
        for (Carta c : cs) out.add(cartaToDto(c));
        return out;
    }

    // ------- Métodos auxiliares originales -------

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

    private Publicacion crearPublicacionEjemplo(String id, String descripcion, Carta cartaOfrecida, BigDecimal precio, List<Carta> cartasInteres, Usuario publicador) {
        Publicacion publicacion = new Publicacion();

        publicacion.setId(id);
        publicacion.setFecha(new Date());
        publicacion.setDescripcion(descripcion);
        publicacion.setCartaOfrecida(cartaOfrecida);
        publicacion.setPrecio(precio);
        publicacion.setCartasInteres(cartasInteres);
        publicacion.setPublicador(publicador);
        publicacion.setEstado(EstadoPublicacion.ACTIVA);

        return publicacion;
    }

}
