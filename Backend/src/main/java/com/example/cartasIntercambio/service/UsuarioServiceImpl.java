package com.example.cartasIntercambio.service;

import com.example.cartasIntercambio.dto.UsuarioDto;
import com.example.cartasIntercambio.model.Usuario.Usuario;
import com.example.cartasIntercambio.repository.UsuarioRepositoryImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioServiceImpl implements IUsuarioService{

    private final UsuarioRepositoryImpl usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepositoryImpl usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void registrarUsuario(UsuarioDto usuarioDto) {
        Usuario usuario = new Usuario();
        usuario.setUser(usuarioDto.getUser());
        usuario.setNombre(usuarioDto.getNombre());
        usuario.setApellido(usuario.getApellido());
        usuario.setEmail(usuarioDto.getCorreo());
        usuario.setPassword(usuarioDto.getPassword());
        usuarioRepository.save(usuario);
    }

    @Override
    public List<UsuarioDto> listarUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();

        return usuarios.stream()
                .map(usuario -> UsuarioDto.builder()
                        .user(usuario.getUser())
                        .nombre(usuario.getNombre())
                        .correo(usuario.getEmail())
                        .password(usuario.getPassword())
                        .build())
                .toList();
    }

}
