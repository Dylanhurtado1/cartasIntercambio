package com.example.cartasIntercambio.service;

import com.example.cartasIntercambio.dto.UsuarioDto;
import com.example.cartasIntercambio.dto.UsuarioResponseDto;
import com.example.cartasIntercambio.exception.UsuarioNoEncontradoException;
import com.example.cartasIntercambio.exception.UsuarioYaExisteException;
import com.example.cartasIntercambio.model.Usuario.Usuario;
import com.example.cartasIntercambio.repository.UsuarioRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class UsuarioServiceImplTest {

    @Mock
    UsuarioRepositoryImpl usuarioRepository;

    @InjectMocks
    UsuarioServiceImpl usuarioService;

    @Test
    void registrarUsuario_ok() {
        // Arrange
        UsuarioDto dto = new UsuarioDto("lucas20", "Lucas", "lucas@email.com", "123");
        when(usuarioRepository.existsByUser("lucas20")).thenReturn(false);
        when(usuarioRepository.existsByCorreo("lucas@email.com")).thenReturn(false);

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
        when(usuarioRepository.existsByCorreo("pepe88@gmail.com")).thenReturn(true);

        // Act & Assert
        assertThrows(UsuarioYaExisteException.class, () -> usuarioService.registrarUsuario(dto));
    }

    @Test
    void listarUsuarios_devuelveDtosCorrectos() {
        // Arrange
        Usuario u = new Usuario();
        u.setId(1L); u.setUser("ana23"); u.setNombre("Ana"); u.setEmail("ana@mail.com");
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
        u.setId(1L); u.setUser("ana23"); u.setNombre("Ana");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(u));

        //Act
        UsuarioResponseDto usuarioResponse = usuarioService.buscarUsuarioPorId(1L);

        //Assert
        assertEquals("ana23", usuarioResponse.getUser());
        assertEquals("Ana", usuarioResponse.getNombre());
    }

    @Test
    void buscarUsuarioPorId_inexistente_lanzaExcepcion() {
        //Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());
        //Act y assert
        assertThrows(UsuarioNoEncontradoException.class, () -> usuarioService.buscarUsuarioPorId(1L));
    }

    @Test
    void actualizarUsuario_ok() {
        // Arrange
        Long id = 7L;
        UsuarioDto dto = new UsuarioDto("lina", "Lina", "lina@mail.com", "xyz");
        Usuario usuarioOriginal = new Usuario();
        usuarioOriginal.setId(id); usuarioOriginal.setUser("viejo"); usuarioOriginal.setNombre("Viejo"); usuarioOriginal.setEmail("viejo@mail.com"); usuarioOriginal.setPassword("abc");
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuarioOriginal));

        // Act
        UsuarioResponseDto actualizado = usuarioService.actualizarUsuario(id, dto);

        // Assert
        assertEquals("lina", actualizado.getUser());
        assertEquals("Lina", actualizado.getNombre());
        assertEquals("lina@mail.com", actualizado.getCorreo());
        verify(usuarioRepository).update(any(Usuario.class));
    }

    @Test
    void actualizarUsuario_inexistente_lanzaExcepcion() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UsuarioNoEncontradoException.class, () -> usuarioService.actualizarUsuario(1L, new UsuarioDto()));
    }

    @Test
    void borrarUsuario_ok() {
        // Arrange
        Usuario u = new Usuario();
        u.setId(3L); u.setUser("paraBorrar");
        when(usuarioRepository.findById(3L)).thenReturn(Optional.of(u));

        // Act
        usuarioService.borrarUsuario(3L);

        // Assert
        verify(usuarioRepository).deleteById(3L);
    }

    @Test
    void borrarUsuario_inexistente_lanzaExcepcion() {
        when(usuarioRepository.findById(3L)).thenReturn(Optional.empty());
        assertThrows(UsuarioNoEncontradoException.class, () -> usuarioService.borrarUsuario(3L));
    }

    @Test
    void buscarUsuarios_filtraPorUser() {
        // Arrange
        Usuario u1 = new Usuario();
        u1.setId(5L); u1.setUser("nico88"); u1.setNombre("Nicolas"); u1.setEmail("nico@mail.com");
        Usuario u2 = new Usuario();
        u2.setId(6L); u2.setUser("juancho"); u2.setNombre("Juan"); u2.setEmail("juan@mail.com");
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


}
