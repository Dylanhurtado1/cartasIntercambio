package com.example.cartasIntercambio.service;

import com.example.cartasIntercambio.dto.CartaDto;
import com.example.cartasIntercambio.dto.PublicacionDto;
import com.example.cartasIntercambio.exception.PublicacionNoEncontradaException;
import com.example.cartasIntercambio.model.Mercado.EstadoPublicacion;
import com.example.cartasIntercambio.model.Mercado.Publicacion;
import com.example.cartasIntercambio.model.Producto_Carta.Carta;
import com.example.cartasIntercambio.model.Producto_Carta.EstadoCarta;
import com.example.cartasIntercambio.model.Usuario.Usuario;
import com.example.cartasIntercambio.repository.irepository.IPublicacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

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
    private Publicacion publicacion1;
    private Publicacion publicacion2;
    private Publicacion publicacion3;

    @Mock
    IPublicacionRepository publicacionRepository;

    @InjectMocks
    PublicacionServiceImpl publicacionService;

    @BeforeEach
    void init() throws ParseException {
        List<String> imagenes = new ArrayList<>();

        // usuarios
        publicador1 = this.crearUsuarioEjemplo("3L", "Juan", "Perez", "juanpe80", "jperez@cartas.com", "JuanPe!2025", "1980-05-04");
        Usuario publicador2 = this.crearUsuarioEjemplo("4L", "Pedro", "Gonzales", "pedro25", "pgonzales@cartas.com", "P3dr0!", "1996-08-11");

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

        publicacion1 = crearPublicacionEjemplo("1L", "Intercambio de cartas Magic", cartaMagic2, BigDecimal.valueOf(50000), cartasDeInteres1, publicador1);
        publicacion2 = crearPublicacionEjemplo("2L", "Intercambio de cartas Yu-Gi-Oh!", cartaYuGiOh3, BigDecimal.valueOf(20000), cartasDeInteres2, publicador1);
        publicacion3 = crearPublicacionEjemplo("3L", "Intercambio de cartas Pokemon", cartaPokemon1, BigDecimal.valueOf(120000), cartasDeInteres3, publicador2);
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
                Arrays.asList(cartaPokemon2, cartaPokemon3), publicador1, EstadoPublicacion.ACTIVA
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
        when(publicacionRepository.findById("2L")).thenReturn(Optional.of(publicacion2));

        PublicacionDto publicacionDtoEncontrada = publicacionService.buscarPublicacionDTOPorId("2L");

        assertNotNull(publicacionDtoEncontrada);
        assertEquals("2L", publicacionDtoEncontrada.getId());
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
        assertEquals("1L", publicacionDTO1.getId());
        assertEquals("Intercambio de cartas Magic", publicacionDTO1.getDescripcion());
        assertEquals("Shivan Dragon", publicacionDTO1.getCartaOfrecida().getNombre());
        assertEquals("Black Lotus", publicacionDTO1.getCartasInteres().get(0).getNombre());
        assertEquals("ACTIVA", publicacionDTO1.getEstado());

        PublicacionDto publicacionDTO2 = publicacionesDTOs.get(1);
        assertEquals("2L", publicacionDTO2.getId());
        assertEquals("Intercambio de cartas Yu-Gi-Oh!", publicacionDTO2.getDescripcion());
        assertEquals("Mirror Force", publicacionDTO2.getCartaOfrecida().getNombre());
        assertEquals("Blue-Eyes White Dragon", publicacionDTO2.getCartasInteres().get(0).getNombre());
        assertEquals("ACTIVA", publicacionDTO2.getEstado());

        PublicacionDto publicacionDTO3 = publicacionesDTOs.get(2);
        assertEquals("3L", publicacionDTO3.getId());
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
        assertEquals("1L", publicacionDTO1.getId());
        assertEquals("Intercambio de cartas Magic", publicacionDTO1.getDescripcion());
        assertEquals("Shivan Dragon", publicacionDTO1.getCartaOfrecida().getNombre());
        assertEquals("Black Lotus", publicacionDTO1.getCartasInteres().get(0).getNombre());
        assertEquals("ACTIVA", publicacionDTO1.getEstado());

        PublicacionDto publicacionDTO2 = publicacionesDTOs.get(1);
        assertEquals("2L", publicacionDTO2.getId());
        assertEquals("Intercambio de cartas Yu-Gi-Oh!", publicacionDTO2.getDescripcion());
        assertEquals("Mirror Force", publicacionDTO2.getCartaOfrecida().getNombre());
        assertEquals("Blue-Eyes White Dragon", publicacionDTO2.getCartasInteres().get(0).getNombre());
        assertEquals("ACTIVA", publicacionDTO2.getEstado());

        verify(publicacionRepository, times(1)).findByPublicadorId(publicador1.getId());
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
