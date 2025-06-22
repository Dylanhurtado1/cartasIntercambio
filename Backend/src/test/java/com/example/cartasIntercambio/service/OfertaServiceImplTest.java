package com.example.cartasIntercambio.service;

import com.example.cartasIntercambio.dto.OfertaDto;
import com.example.cartasIntercambio.exception.PublicacionNoEncontradaException;
import com.example.cartasIntercambio.model.Mercado.EstadoOferta;
import com.example.cartasIntercambio.model.Mercado.EstadoPublicacion;
import com.example.cartasIntercambio.model.Mercado.Oferta;
import com.example.cartasIntercambio.model.Mercado.Publicacion;
import com.example.cartasIntercambio.model.Producto_Carta.Carta;
import com.example.cartasIntercambio.model.Producto_Carta.EstadoCarta;
import com.example.cartasIntercambio.model.Usuario.Usuario;
import com.example.cartasIntercambio.repository.irepository.IOfertaRepository;
import com.example.cartasIntercambio.repository.irepository.IPublicacionRepository;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class OfertaServiceImplTest {
    private Carta cartaMagicPublicada;
    private Carta cartaYuGiOhPublicada;
    private Carta cartaMagicInteres1;
    private Carta cartaMagicInteres2;
    private Carta cartaYuGiOhInteres1;
    private Carta cartaYuGiOhInteres2;
    private Usuario publicador;
    private Usuario ofertante1;
    private Usuario ofertante2;
    private Publicacion publicacionActiva;
    private Publicacion publicacionFinalizada;
    private Oferta oferta1;
    private Oferta oferta2;
    private Oferta oferta3;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private IOfertaRepository ofertaRepository;

    @Mock
    private IPublicacionRepository publicacionRepository;

    @InjectMocks
    private OfertaServiceImpl ofertaService;

    @BeforeEach
    void init() throws ParseException {
        List<String> imagenes = new ArrayList<>();

        // usuarios
        publicador = this.crearUsuarioEjemplo("1", "Juan", "Perez", "juanpe80", "jperez@cartas.com", "JuanPe!2025", "1980-05-04");
        ofertante1 = this.crearUsuarioEjemplo("2", "Pedro", "Gonzales", "pedro25", "pgonzales@cartas.com", "P3dr0!", "1996-08-11");
        ofertante2 = this.crearUsuarioEjemplo("3", "Mora", "Rodriguez", "morodriguez", "mrodriguez@cartas.com", "M0r1t@!", "1996-08-11");

        // cartas ofrecidas
        cartaMagicPublicada = new Carta("Magic", "Shivan Dragon", EstadoCarta.MALO, imagenes);
        cartaYuGiOhPublicada = new Carta("YuGiOh", "Mirror Force", EstadoCarta.MUY_BUENO, imagenes);

        cartaMagicInteres1 = new Carta("Magic", "Black Lotus", EstadoCarta.BUENO, imagenes);
        cartaMagicInteres2 = new Carta("Magic", "Llanowar Elves", EstadoCarta.MUY_MALO, imagenes);

        cartaYuGiOhInteres1 = new Carta("YuGiOh", "Blue-Eyes White Dragon", EstadoCarta.MALO, imagenes);
        cartaYuGiOhInteres2 = new Carta("YuGiOh", "Dark Magician", EstadoCarta.NUEVO, imagenes);

        // publicaciones
        List<Carta> cartasDeInteres1 = Arrays.asList(cartaMagicInteres1, cartaMagicInteres2);
        List<Carta> cartasDeInteres2 = Arrays.asList(cartaYuGiOhInteres1, cartaYuGiOhInteres2);

        publicacionActiva = crearPublicacionEjemplo("1", "Intercambio de cartas Magic", cartaMagicPublicada, BigDecimal.valueOf(50000), cartasDeInteres1, publicador, EstadoPublicacion.ACTIVA);
        publicacionFinalizada = crearPublicacionEjemplo("2", "Intercambio de cartas Yu-Gi-Oh!", cartaYuGiOhPublicada, BigDecimal.valueOf(20000), cartasDeInteres2, publicador, EstadoPublicacion.FINALIZADA);

        // ofertas
        List<Carta> cartasOfrecidas1 = Arrays.asList(cartaMagicInteres2);
        List<Carta> cartasOfrecidas2 = Arrays.asList(cartaYuGiOhInteres1, cartaYuGiOhInteres2);

        // ofertas del ofertante 1
        oferta1 = crearOfertaEjemplo("1", publicacionActiva.getId(), BigDecimal.valueOf(45000), cartasOfrecidas1, ofertante1);
        oferta2 = crearOfertaEjemplo("2", publicacionActiva.getId(), BigDecimal.valueOf(79500), cartasOfrecidas2, ofertante1);
    }

    // ---------------------------------------- TESTS ---------------------------------------- //

    @Test
    void testCrearOferta() {
        Date fechaDeHoy = new Date();
        BigDecimal monto = BigDecimal.valueOf(45000);
        List<Carta> cartasOfrecidas = Arrays.asList(cartaMagicInteres1);

        // Crear oferta mock
        Oferta ofertaMock = new Oferta(fechaDeHoy, "1L", monto, cartasOfrecidas,
                ofertante1, EstadoOferta.PENDIENTE);
        OfertaDto ofertaDtoMock = this.ofertaAOfertaDTO(ofertaMock);

        when(ofertaRepository.save(any(Oferta.class))).thenReturn(ofertaMock);

        ofertaService.crearOferta(ofertaDtoMock, publicacionActiva);

        ArgumentCaptor<Oferta> captor = ArgumentCaptor.forClass(Oferta.class);
        verify(ofertaRepository, times(1)).save(captor.capture());

        Oferta ofertaGuardada = captor.getValue();

        assertEquals(fechaDeHoy, ofertaGuardada.getFecha());
        assertEquals(monto, ofertaGuardada.getMonto());
        assertEquals("1", ofertaGuardada.getIdPublicacion());
        assertEquals(cartasOfrecidas, ofertaGuardada.getCartasOfrecidas());
        assertEquals(ofertante1, ofertaGuardada.getOfertante());
        assertEquals(EstadoOferta.PENDIENTE, ofertaGuardada.getEstado());
        assertEquals("Black Lotus", ofertaGuardada.getCartasOfrecidas().get(0).getNombre());
    }

    // Si se intenta ofertar una publicación finalizada, tira error BAD REQUEST "La publicación ya no está activa."
    @Test
    void testOfertarPublicacionFinalizada_LanzaExcepcion() {
        OfertaDto ofertaDTO = ofertaAOfertaDTO(oferta1);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            ofertaService.crearOferta(ofertaDTO, publicacionFinalizada);
        });

        assertTrue(exception.getMessage().contains("La publicación ya no está activa."));
    }

    @Test
    void buscarOfertaPorID() {
        String idOferta = "1";

        when(ofertaRepository.findById(idOferta)).thenReturn(Optional.of(oferta1));

        Oferta ofertaEncontrada = ofertaService.buscarOfertaPorId(idOferta); // buscamos la oferta1

        assertNotNull(ofertaEncontrada);
        assertEquals(idOferta, ofertaEncontrada.getId());
        verify(ofertaRepository, times(1)).findById(idOferta);
        assertEquals(publicacionActiva.getId(), ofertaEncontrada.getIdPublicacion());
    }

    // Si se intenta buscar una oferta que no existe tira error NOT_FOUND "No existe la oferta con id: " + idOferta
    @Test
    void buscarOfertaPorID_NoExiste_LanzaExcepcion() {
        String idOferta = "5";

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            ofertaService.buscarOfertaPorId(idOferta);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("No existe la oferta con id: " + idOferta));
    }

    @Test
    void buscarOfertasRecibidasEnUnaPublicacionDeUnUsuario() {
        String idUsuarioPublicador = "1";
        String idPublicacion = publicacionActiva.getId();

        List<Oferta> ofertasRecibidas = List.of(oferta1, oferta2);

        when(ofertaRepository.findByIdPublicacion("1")).thenReturn(ofertasRecibidas);

        List<OfertaDto> resultado = ofertaService.buscarOfertasPorPublicacion(publicacionActiva, idUsuarioPublicador);

        assertEquals(2, resultado.size());
        assertEquals("1", resultado.get(0).getId());
        assertEquals("2", resultado.get(1).getId());
    }

    // Si un usuario que no es el publicador intenta ver las ofertas recibidas
    // tira error FORBIDDEN "No tiene permiso para ver esta publicación"
    @Test
    void usuarioNoPublicadorIntentaVerOfertasRecibidas_LanzaExcepcion() {
        String idUsuario = "3"; // id del ofertante 2, cuando el publicador es el usuario con id 1L

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            ofertaService.buscarOfertasPorPublicacion(publicacionActiva, idUsuario);
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertTrue(exception.getReason().contains("No tiene permiso para ver esta publicación"));
    }

    @Test
    void buscarOfertasRecibidasEnUnaPublicacion() {
        String idPublicacion = "1";

        when(publicacionRepository.existsById(idPublicacion)).thenReturn(true);

        List<Oferta> ofertasRecibidas = List.of(oferta1, oferta2);

        when(ofertaRepository.findByIdPublicacion(idPublicacion)).thenReturn(ofertasRecibidas);

        List<OfertaDto> resultado = ofertaService.buscarOfertasPorPublicacion(idPublicacion);

        assertEquals(2, resultado.size());
        assertEquals("1", resultado.get(0).getId());
        assertEquals("2", resultado.get(1).getId());

        verify(publicacionRepository).existsById(idPublicacion); // verifica que se haya llamado al metodo
        verify(ofertaRepository).findByIdPublicacion(idPublicacion);
    }

    // Si se intenta ver las ofertas recibidas en una publicacion NO existente
    // tira error PublicacionNoEncontradaException "No existe la publicación con id: " + idPublicacion
    @Test
    void verOfertasRecibidasEnPublicacionNOExistente_LanzaExcepcion() {
        String idPublicacion = "7";

        PublicacionNoEncontradaException exception = assertThrows(PublicacionNoEncontradaException.class, () -> {
            ofertaService.buscarOfertasPorPublicacion(idPublicacion);
        });

        assertTrue(exception.getMessage().contains("No existe la publicación con id: " + idPublicacion));

        verify(publicacionRepository).existsById(idPublicacion); // verifica que se haya llamado al metodo
        verifyNoInteractions(ofertaRepository);
    }

    @Test
    void buscarOfertasRealizadas() {
        String idOfertante = "2"; // id ofertante 1 que realizo las ofertas 1 y 2

        List<Oferta> ofertasRealizadas = List.of(oferta1, oferta2);

        when(ofertaRepository.findByOfertante_Id(idOfertante)).thenReturn(ofertasRealizadas);

        List<OfertaDto> resultado = ofertaService.buscarOfertasRealizadas(idOfertante);

        assertEquals(2, resultado.size());
        assertEquals("1", resultado.get(0).getId());
        assertEquals("2", resultado.get(1).getId());
        assertEquals(oferta1.getIdPublicacion(), resultado.get(0).getIdPublicacion());
        assertEquals(oferta2.getIdPublicacion(), resultado.get(1).getIdPublicacion());

        verify(ofertaRepository, times(1)).findByOfertante_Id(idOfertante);
    }

    @Test
    void testGuardarOferta() {
        oferta1.setEstado(EstadoOferta.ACEPTADO);

        // Simulamos que se encuentra y modifica la oferta1 de PENDIENTE a ACEPTADO
        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class), eq(Oferta.class)))
                .thenReturn(oferta1);

        assertDoesNotThrow(() -> ofertaService.guardarOferta(oferta1));

        // Verificamos que Mongo Template haya sido llamado ok
        verify(mongoTemplate).findAndModify(any(Query.class), any(Update.class), eq(Oferta.class));
    }

    @Test
    void testGuardarOferta_YafueModificadaPorOtroUsuario_LanzaExcepcion() {
        oferta1.setEstado(EstadoOferta.ACEPTADO);

        // Simulamos que no se encontro la oferta para actualizar
        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class), eq(Oferta.class)))
                .thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> ofertaService.guardarOferta(oferta1));

        assertEquals("La oferta fue modificada por otro usuario (o no existe).", ex.getMessage());
    }

    @Test
    void testRechazarOtrasOfertas() {
        String idOferta = "1"; // id de la oferta 1
        String idPublicacion = "1"; // id de la publicacion activa que tiene la oferta 1 y 2

        ofertaService.rechazarOtrasOfertas(idOferta, idPublicacion);

        // Verificamos que se haya llamado updateMulti con los valores correctos
        ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
        ArgumentCaptor<Update> updateCaptor = ArgumentCaptor.forClass(Update.class);

        verify(mongoTemplate).updateMulti(queryCaptor.capture(), updateCaptor.capture(), eq(Oferta.class));

        Query queryUsada = queryCaptor.getValue();
        Update updateUsado = updateCaptor.getValue();

        Document setDoc = (Document) updateUsado.getUpdateObject().get("$set");

        // Validamos que la query tiene la publicación correcta y excluye el id de la oferta
        assertTrue(queryUsada.getQueryObject().toString().contains("idPublicacion"));
        assertTrue(queryUsada.getQueryObject().toString().contains(idPublicacion));
        assertTrue(queryUsada.getQueryObject().toString().contains("$ne")); // excluye el id de la oferta 1

        // Validamos que se actualiza el estado de la oferta a RECHAZADO
        assertEquals(EstadoOferta.RECHAZADO, setDoc.get("estado"));
    }

    @Test
    void testRechazarOtrasOfertas_SinOtrasOfertas() {
        String idOferta = "1"; // id de la oferta 1
        String idPublicacion = "1"; // id de la publicacion activa que tiene la oferta 1 y 2

        // Simulamos resultado sin modificaciones para cuando no hay otras ofertas que rechazar
        UpdateResult updateResult = mock(UpdateResult.class);

        when(mongoTemplate.updateMulti(any(Query.class), any(Update.class), eq(Oferta.class)))
                .thenReturn(updateResult);

        ofertaService.rechazarOtrasOfertas(idOferta, idPublicacion);

        // Verificamos que se llam a updateMulti aunque no haya modificado nada
        verify(mongoTemplate).updateMulti(any(Query.class), any(Update.class), eq(Oferta.class));
    }

    @Test
    void testBuscarOfertaDTO() {
        String idOferta = oferta1.getId();

        when(ofertaRepository.findById(idOferta)).thenReturn(Optional.of(oferta1));

        OfertaDto ofertaEncontrada = ofertaService.buscarOfertaDto(idOferta);

        assertNotNull(ofertaEncontrada);
        assertEquals(oferta1.getId(), ofertaEncontrada.getId());
        assertEquals(oferta1.getMonto(), ofertaEncontrada.getMonto());
        assertEquals(oferta1.getEstado().toString(), ofertaEncontrada.getEstado());
        assertEquals(oferta1.getCartasOfrecidas().get(0).getNombre(), ofertaEncontrada.getCartasOfrecidas().get(0).getNombre());
        assertEquals(oferta1.getIdPublicacion(), ofertaEncontrada.getIdPublicacion());
    }

    @Test
    void testBuscarOfertaDTO_NoExiste_LanzaExcepcion() {
        when(ofertaRepository.findById("10")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> ofertaService.buscarOfertaDto("10")
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("No existe la oferta con id: " + "10", ex.getReason());
    }

    // -------------------------------------------------------------------------------------------- //
    // ------------------------------------ Metodos Auxiliares ------------------------------------ //

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

    private Publicacion crearPublicacionEjemplo(String id, String descripcion, Carta cartaOfrecida, BigDecimal precio, List<Carta> cartasInteres, Usuario publicador, EstadoPublicacion estadoPublicacion) {
        Publicacion publicacion = new Publicacion();

        publicacion.setId(id);
        publicacion.setFecha(new Date());
        publicacion.setDescripcion(descripcion);
        publicacion.setCartaOfrecida(cartaOfrecida);
        publicacion.setPrecio(precio);
        publicacion.setCartasInteres(cartasInteres);
        publicacion.setPublicador(publicador);
        publicacion.setEstado(estadoPublicacion);

        return publicacion;
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

    public OfertaDto ofertaAOfertaDTO(Oferta oferta) {

        OfertaDto ofertaDTO = new OfertaDto(
                oferta.getFecha(),
                oferta.getIdPublicacion(),
                oferta.getMonto(),
                oferta.getCartasOfrecidas(),
                oferta.getOfertante(),
                "PENDIENTE"
        );

        return ofertaDTO;
    }

}