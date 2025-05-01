package com.example.cartasIntercambio.service;

import com.example.cartasIntercambio.dto.UsuarioDto;
import com.example.cartasIntercambio.dto.UsuarioResponseDto;
import com.example.cartasIntercambio.exception.UsuarioNoEncontradoException;
import com.example.cartasIntercambio.exception.UsuarioYaExisteException;
import com.example.cartasIntercambio.model.Usuario.Usuario;
import com.example.cartasIntercambio.repository.UsuarioRepositoryImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements IUsuarioService{

    private final UsuarioRepositoryImpl usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepositoryImpl usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void registrarUsuario(UsuarioDto usuarioDto) {
        if (usuarioRepository.existsByUser(usuarioDto.getUser()) || usuarioRepository.existsByCorreo(usuarioDto.getCorreo())) {
            throw new UsuarioYaExisteException("Ya existe un usuario con ese user y/o correo");
        }
        Usuario usuario = new Usuario();
        usuario.setUser(usuarioDto.getUser());
        usuario.setNombre(usuarioDto.getNombre());
        usuario.setApellido(usuario.getApellido());
        usuario.setEmail(usuarioDto.getCorreo());
        usuario.setPassword(usuarioDto.getPassword());
        usuarioRepository.save(usuario);
    }

    @Override
    public List<UsuarioResponseDto> listarUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();

        return usuarios.stream()
                .map(usuario -> UsuarioResponseDto.builder()
                        .user(usuario.getUser())
                        .nombre(usuario.getNombre())
                        .correo(usuario.getEmail())
                        .build())
                .toList();
    }

    @Override
    public UsuarioResponseDto buscarUsuarioPorId(Long id) {
        Optional<Usuario> usuarioOpcional = usuarioRepository.findById(id);
        if(usuarioOpcional.isEmpty()) {
            throw new UsuarioNoEncontradoException("Usuario con id " + id + " no existe");
        }
        Usuario usuario = usuarioOpcional.get();
        return UsuarioResponseDto.builder()
                .user(usuario.getUser())
                .nombre(usuario.getNombre())
                .correo(usuario.getEmail())
                .build();
    }

}
