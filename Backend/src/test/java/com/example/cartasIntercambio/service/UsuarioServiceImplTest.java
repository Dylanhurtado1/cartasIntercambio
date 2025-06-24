package com.example.cartasIntercambio.service;

import com.example.cartasIntercambio.dto.UsuarioDto;
import com.example.cartasIntercambio.dto.UsuarioResponseDto;
import com.example.cartasIntercambio.exception.UsuarioNoEncontradoException;
import com.example.cartasIntercambio.exception.UsuarioYaExisteException;
import com.example.cartasIntercambio.jwt.JwtUtil;
import com.example.cartasIntercambio.model.Usuario.Usuario;
import com.example.cartasIntercambio.repository.irepository.IUsuarioRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class UsuarioServiceImplTest {

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @Mock
    private IUsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private CookieService cookieService;

    @Mock
    private HttpServletResponse response;

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
    void testLogin() {
        Date fechaNacimiento = null;
        Usuario usuario = new Usuario("1", "Morita", "Mora", "Gonzales", "mgonzales@cartas.com", "123456", fechaNacimiento);
        UsuarioDto usuarioDTO = new UsuarioDto("Morita", "Mora", "mgonzales@cartas.com", "123456");

        usuario.setPassword("123456");

        when(usuarioRepository.findByUser(usuario.getUser())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("123456", usuario.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(usuario.getId(), usuario.getUser())).thenReturn("test-jwt");
        doNothing().when(cookieService).addHttpCookie(eq("jwt"), eq("test-jwt"), anyInt(), eq(response));

        UsuarioResponseDto usuarioResponseDto = usuarioService.login(usuarioDTO, response);

        assertNotNull(usuarioResponseDto);
        assertEquals(usuario.getId(), usuarioResponseDto.getId());
        assertEquals(usuario.getUser(), usuarioResponseDto.getUser());
        assertEquals("USER", usuarioResponseDto.getTipo());

        verify(cookieService).addHttpCookie("jwt", "test-jwt", 3600, response);
    }

    @Test
    void testLogin_UsuarioNoExiste_LanzaExcepcion() {
        UsuarioDto usuarioDTO = new UsuarioDto();

        usuarioDTO.setUser("Usuario");
        usuarioDTO.setPassword("Password");

        when(usuarioRepository.findByUser("Usuario")).thenReturn(Optional.empty());

        UsuarioResponseDto usuarioResponseDto = usuarioService.login(usuarioDTO, response);

        assertNull(usuarioResponseDto);
    }

    @Test
    void testLogin_CredencialesIncorrectas_LanzaExcepcion() {
        Date fechaNacimiento = null;
        Usuario usuario = new Usuario("1", "Morita", "Mora", "Gonzales", "mgonzales@cartas.com", "123456", fechaNacimiento);
        UsuarioDto usuarioDTO = new UsuarioDto("Morita", "Mora", "mgonzales@cartas.com", "123456");

        usuarioDTO.setUser(usuario.getUser());
        usuarioDTO.setPassword("123456");

        usuario.setPassword("123456");

        when(usuarioRepository.findByUser(usuario.getUser())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("123456", usuario.getPassword())).thenReturn(false);

        UsuarioResponseDto usuarioResponseDto = usuarioService.login(usuarioDTO, response);

        assertNull(usuarioResponseDto);
    }

    @Test
    void testLogout() {
        usuarioService.logout(response);

        verify(cookieService).deleteHttpCookie("jwt", response);
    }

}