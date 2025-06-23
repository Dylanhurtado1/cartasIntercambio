package com.example.cartasIntercambio.service;

import com.example.cartasIntercambio.dto.UsuarioDto;
import com.example.cartasIntercambio.dto.UsuarioResponseDto;
import com.example.cartasIntercambio.exception.UsuarioNoEncontradoException;
import com.example.cartasIntercambio.exception.UsuarioYaExisteException;
import com.example.cartasIntercambio.model.Usuario.Admin;
import com.example.cartasIntercambio.model.Usuario.Usuario;
import com.example.cartasIntercambio.repository.irepository.IUsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class UsuarioServiceImplTest {

    @Mock
    IUsuarioRepository usuarioRepository;

    @InjectMocks
    UsuarioServiceImpl usuarioService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void init() {
        // Reemplazamos el passwordEncoder manualmente con un mock
        ReflectionTestUtils.setField(usuarioService, "passwordEncoder", passwordEncoder);
    }

    // ---------------------------------------- TESTS ---------------------------------------- //

    @Test
    void registrarUsuario_ok() {
        // Arrange
        UsuarioDto dto = new UsuarioDto("lucas20", "Lucas", "lucas@email.com", "123");
        when(usuarioRepository.existsByUser("lucas20")).thenReturn(false);
        when(usuarioRepository.existsByEmail("lucas@email.com")).thenReturn(false);

        Usuario usuarioMock = new Usuario();
        usuarioMock.setId("algunaId");
        usuarioMock.setUser("lucas20");
        usuarioMock.setNombre("Lucas");
        usuarioMock.setEmail("lucas@email.com");
        usuarioMock.setPassword("123");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);
        // Act
        usuarioService.registrarUsuario(dto);

        // Assert
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void registrarUsuario_yaExiste_lanzaExcepcion() {
        // Arrange
        UsuarioDto dto = new UsuarioDto("pepe88", "Pepe", "pepe88@email.com", "clave");
        when(usuarioRepository.existsByUser("pepe88")).thenReturn(true);

        // Act & Assert
        assertThrows(UsuarioYaExisteException.class, () -> usuarioService.registrarUsuario(dto));
    }

    @Test
    void registrarUsuario_yaExisteCorreo_lanzaExcepcion() {
        // Arrange
        UsuarioDto dto = new UsuarioDto("pepe88", "Pepe", "pepe88@gmail.com", "clave");
        when(usuarioRepository.existsByEmail("pepe88@gmail.com")).thenReturn(true);

        // Act & Assert
        assertThrows(UsuarioYaExisteException.class, () -> usuarioService.registrarUsuario(dto));
    }

    @Test
    void listarUsuarios_devuelveDtosCorrectos() {
        // Arrange
        Usuario u = new Usuario();
        u.setId("1"); u.setUser("ana23"); u.setNombre("Ana"); u.setEmail("ana@mail.com");
        when(usuarioRepository.findAll()).thenReturn(List.of(u));

        // Act
        List<UsuarioResponseDto> resultado = usuarioService.listarUsuarios();

        // Assert
        assertEquals(1, resultado.size());
        assertEquals("ana23", resultado.get(0).getUser());
    }

    @Test
    void obtenerUsuarioPorId(){
        //Arrange
        Usuario u = new Usuario();
        u.setId("1"); u.setUser("ana23"); u.setNombre("Ana");
        when(usuarioRepository.findById("1")).thenReturn(Optional.of(u));

        //Act
        UsuarioResponseDto usuarioResponse = usuarioService.buscarUsuarioPorId("1");

        //Assert
        assertEquals("ana23", usuarioResponse.getUser());
        assertEquals("Ana", usuarioResponse.getNombre());
    }

    @Test
    void buscarUsuarioPorId_inexistente_lanzaExcepcion() {
        //Arrange
        when(usuarioRepository.findById("1")).thenReturn(Optional.empty());
        //Act y assert
        assertThrows(UsuarioNoEncontradoException.class, () -> usuarioService.buscarUsuarioPorId("1"));
    }

    @Test
    void actualizarUsuario_ok() {
        // Arrange
        String id = "7";
        UsuarioDto dto = new UsuarioDto("lina", "Lina", "lina@mail.com", "xyz");
        Usuario usuarioOriginal = new Usuario();
        usuarioOriginal.setId(id); usuarioOriginal.setUser("viejo"); usuarioOriginal.setNombre("Viejo"); usuarioOriginal.setEmail("viejo@mail.com"); usuarioOriginal.setPassword("abc");
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuarioOriginal));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioOriginal);
        // Act
        UsuarioResponseDto actualizado = usuarioService.actualizarUsuario(id, dto);

        // Assert
        assertEquals("lina", actualizado.getUser());
        assertEquals("Lina", actualizado.getNombre());
        assertEquals("lina@mail.com", actualizado.getCorreo());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void actualizarUsuario_inexistente_lanzaExcepcion() {
        when(usuarioRepository.findById("1")).thenReturn(Optional.empty());
        assertThrows(UsuarioNoEncontradoException.class, () -> usuarioService.actualizarUsuario("1", new UsuarioDto()));
    }

    @Test
    void borrarUsuario_ok() {
        // Arrange
        String id = "3";
        when(usuarioRepository.existsById(id)).thenReturn(true);

        // Act
        usuarioService.borrarUsuario(id);

        // Assert
        verify(usuarioRepository).deleteById(id);
    }


    @Test
    void borrarUsuario_inexistente_lanzaExcepcion() {
        when(usuarioRepository.existsById("3")).thenReturn(false);
        assertThrows(UsuarioNoEncontradoException.class, () -> usuarioService.borrarUsuario("3"));
    }

    @Test
    void buscarUsuarios_filtraPorUser() {
        // Arrange
        Usuario u1 = new Usuario();
        u1.setId("5"); u1.setUser("nico88"); u1.setNombre("Nicolas"); u1.setEmail("nico@mail.com");
        Usuario u2 = new Usuario();
        u2.setId("6"); u2.setUser("juancho"); u2.setNombre("Juan"); u2.setEmail("juan@mail.com");
        when(usuarioRepository.findAll()).thenReturn(List.of(u1, u2));

        // Act
        List<UsuarioResponseDto> resultado = usuarioService.buscarUsuarios("nico", null, null);

        // Assert
        assertEquals(1, resultado.size());
        assertEquals("nico88", resultado.get(0).getUser());
    }

    @Test
    void buscarUsuarios_filtraPorNombre() {
        // Arrange
        Usuario u1 = new Usuario(); u1.setUser("nico88"); u1.setNombre("Nicolas"); u1.setEmail("nico@mail.com");
        Usuario u2 = new Usuario(); u2.setUser("juancho"); u2.setNombre("Juan"); u2.setEmail("juan@mail.com");
        when(usuarioRepository.findAll()).thenReturn(List.of(u1, u2));

        // Act
        List<UsuarioResponseDto> resultado = usuarioService.buscarUsuarios(null, "Juan", null);

        // Assert
        assertEquals(1, resultado.size());
        assertEquals("juancho", resultado.get(0).getUser());
    }

    @Test
    void buscarUsuarios_filtraPorCorreo() {
        // Arrange
        Usuario u1 = new Usuario(); u1.setUser("luli11"); u1.setNombre("Luli"); u1.setEmail("luli@abc.com");
        Usuario u2 = new Usuario(); u2.setUser("sofi22"); u2.setNombre("Sofi"); u2.setEmail("sofi@email.com");
        when(usuarioRepository.findAll()).thenReturn(List.of(u1, u2));

        // Act
        List<UsuarioResponseDto> resultado = usuarioService.buscarUsuarios(null, null, "sofi@email.com");

        // Assert
        assertEquals(1, resultado.size());
        assertEquals("sofi22", resultado.get(0).getUser());
    }

    @Test
    void crearAdmin_ok() {
        // Arrange
        UsuarioDto dto = new UsuarioDto("admin44", "Olga", "olga@admin.com", "securePass");
        when(usuarioRepository.existsByUser("admin44")).thenReturn(false);
        when(usuarioRepository.existsByEmail("olga@admin.com")).thenReturn(false);

        // Act
        UsuarioResponseDto resp = usuarioService.crearAdmin(dto);

        // Assert
        assertEquals("admin44", resp.getUser());
        assertEquals("Olga", resp.getNombre());
        assertEquals("olga@admin.com", resp.getCorreo());
        assertEquals("admin", resp.getTipo());
        verify(usuarioRepository).save(any(Admin.class));
    }

    @Test
    void crearAdmin_yaExiste_lanzaExcepcion() {
        // Arrange
        UsuarioDto dto = new UsuarioDto("admin44", "Olga", "olga@admin.com", "securePass");
        when(usuarioRepository.existsByUser("admin44")).thenReturn(true);

        // Act & Assert
        assertThrows(UsuarioYaExisteException.class, () -> usuarioService.crearAdmin(dto));
    }

    @Test
    void crearAdmin_yaExistePorCorreo_lanzaExcepcion() {
        // Arrange
        UsuarioDto dto = new UsuarioDto("admin44", "Olga", "olga@admin.com", "securePass");
        when(usuarioRepository.existsByEmail("olga@admin.com")).thenReturn(true);

        // Act & Assert
        assertThrows(UsuarioYaExisteException.class, () -> usuarioService.crearAdmin(dto));
    }

    @Test
    void listarUsuarios_incluyeAdmin() {
        // Arrange
        Admin admin = new Admin();
        admin.setId("1");
        admin.setUser("admin1");
        admin.setNombre("SuperAdmin");
        admin.setEmail("admin@mail.com");

        when(usuarioRepository.findAll()).thenReturn(List.of(admin));

        // Act
        List<UsuarioResponseDto> resultado = usuarioService.listarUsuarios();

        // Assert
        assertEquals(1, resultado.size());
        assertEquals("admin", resultado.get(0).getTipo());
    }


    @Test
    void buscarUsuarioPorId_admin_devuelveTipoAdmin() {
        // Arrange
        Admin admin = new Admin();
        admin.setId("1");
        admin.setUser("admin1");
        admin.setNombre("SuperAdmin");
        admin.setEmail("admin@mail.com");
        when(usuarioRepository.findById("1")).thenReturn(Optional.of(admin));

        // Act
        UsuarioResponseDto resp = usuarioService.buscarUsuarioPorId("1");

        // Assert
        assertEquals("admin", resp.getTipo());
    }

    @Test
    void actualizarUsuario_adminActualizaTipoAdmin() {
        // Arrange
        String id = "99";
        Admin admin = new Admin();
        admin.setId(id);
        admin.setUser("admin1");
        admin.setNombre("SuperAdmin");
        admin.setEmail("admin1@mail.com");
        admin.setPassword("oldpass");

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(admin));
        when(usuarioRepository.save(any(Admin.class))).thenReturn(admin);

        UsuarioDto dto = new UsuarioDto("adminNEW", "Admin Nuevo", "adminnuevo@mail.com", "newpass");

        // Act
        UsuarioResponseDto resp = usuarioService.actualizarUsuario(id, dto);

        // Assert
        assertEquals("admin", resp.getTipo());
        assertEquals("adminNEW", resp.getUser());
        assertEquals("Admin Nuevo", resp.getNombre());
        assertEquals("adminnuevo@mail.com", resp.getCorreo());
        verify(usuarioRepository).save(any(Admin.class));
    }

    @Test
    void buscarUsuarios_incluyeAdminYRetornaTipoCorrecto() {
        // Arrange
        Admin admin = new Admin();
        admin.setId("1");
        admin.setUser("admin1");
        admin.setNombre("SuperAdmin");
        admin.setEmail("admin@mail.com");

        Usuario user = new Usuario();
        user.setId("2");
        user.setUser("usuario1");
        user.setNombre("Usuario");
        user.setEmail("usuario@mail.com");

        List<Usuario> lista = List.of(admin, user);

        when(usuarioRepository.findAll()).thenReturn(lista);

        // Act
        List<UsuarioResponseDto> resultado = usuarioService.buscarUsuarios(null, null, null);

        // Assert
        assertEquals(2, resultado.size());

        // Chequeo individual por tipo
        UsuarioResponseDto respAdmin = resultado.stream().filter(r -> r.getUser().equals("admin1")).findFirst().get();
        UsuarioResponseDto respUser = resultado.stream().filter(r -> r.getUser().equals("usuario1")).findFirst().get();

        assertEquals("admin", respAdmin.getTipo());
        assertEquals("usuario", respUser.getTipo());
    }

//    @Test
//    void testLogin() throws Exception {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        Date fechaNacimiento = sdf.parse("2013-11-13");
//        Usuario usuario = new Usuario("1", "Morita", "Mora", "Gonzales", "mgonzales@cartas.com", "123456", fechaNacimiento);
//        UsuarioDto usuarioDTO = new UsuarioDto("Morita", "Mora", "mgonzales@cartas.com", "123456");
//
//        usuario.setPassword(new BCryptPasswordEncoder().encode("123456"));
//
//        when(usuarioRepository.findByUser(usuario.getUser())).thenReturn(Optional.of(usuario));
//        when(passwordEncoder.matches("123456", usuario.getPassword())).thenReturn(true);
//
//        UsuarioResponseDto usuarioResponseDto = usuarioService.login(usuarioDTO);
//
//        assertNotNull(usuarioResponseDto);
//        assertEquals(usuario.getId(), usuarioResponseDto.getId());
//        assertEquals(usuario.getUser(), usuarioResponseDto.getUser());
//        assertEquals("usuario", usuarioResponseDto.getTipo());
//    }

//    @Test
//    void testLogin_UsuarioNoExiste_LanzaExcepcion() {
//        UsuarioDto usuarioDTO = new UsuarioDto();
//
//        usuarioDTO.setUser("Usuario");
//        usuarioDTO.setPassword("Password");
//
//        when(usuarioRepository.findByUser("Usuario")).thenReturn(Optional.empty());
//
//        UsuarioResponseDto response = usuarioService.login(usuarioDTO);
//
//        assertNull(response);
//    }

//    @Test
//    void testLogin_CredencialesIncorrectas_LanzaExcepcion() throws Exception {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        Date fechaNacimiento = sdf.parse("2013-11-13");
//        Usuario usuario = new Usuario("1", "Morita", "Mora", "Gonzales", "mgonzales@cartas.com", "123456", fechaNacimiento);
//        UsuarioDto usuarioDTO = new UsuarioDto("Morita", "Mora", "mgonzales@cartas.com", "123456");
//
//        usuarioDTO.setUser(usuario.getUser());
//        usuarioDTO.setPassword("123456");
//
//        usuario.setPassword(new BCryptPasswordEncoder().encode("123456"));
//
//        when(usuarioRepository.findByUser(usuario.getUser())).thenReturn(Optional.of(usuario));
//        when(passwordEncoder.matches("123456", usuario.getPassword())).thenReturn(false);
//
//        UsuarioResponseDto response = usuarioService.login(usuarioDTO);
//
//        assertNull(response);
//    }

}
