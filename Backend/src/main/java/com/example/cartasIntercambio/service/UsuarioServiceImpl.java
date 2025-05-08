package com.example.cartasIntercambio.service;

import com.example.cartasIntercambio.dto.UsuarioDto;
import com.example.cartasIntercambio.dto.UsuarioResponseDto;
import com.example.cartasIntercambio.exception.UsuarioNoEncontradoException;
import com.example.cartasIntercambio.exception.UsuarioYaExisteException;
import com.example.cartasIntercambio.model.Usuario.Admin;
import com.example.cartasIntercambio.model.Usuario.Usuario;
import com.example.cartasIntercambio.repository.irepository.IUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements IUsuarioService{

    private final IUsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioServiceImpl(IUsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void registrarUsuario(UsuarioDto usuarioDto) {
        if (usuarioRepository.existsByUser(usuarioDto.getUser()) || usuarioRepository.existsByEmail(usuarioDto.getCorreo())) {
            throw new UsuarioYaExisteException("Ya existe un usuario con ese user y/o correo");
        }
        Usuario usuario = new Usuario();
        usuario.setUser(usuarioDto.getUser());
        usuario.setNombre(usuarioDto.getNombre());
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
                        .tipo(usuario instanceof Admin ? "admin" : "usuario")
                        .build())
                .toList();
    }

    @Override
    public UsuarioResponseDto buscarUsuarioPorId(String id) {
        Optional<Usuario> usuarioOpcional = usuarioRepository.findById(id);
        if (usuarioOpcional.isEmpty()) {
            throw new UsuarioNoEncontradoException("Usuario con id " + id + " no existe");
        }
        Usuario usuario = usuarioOpcional.get();
        return UsuarioResponseDto.builder()
                .user(usuario.getUser())
                .nombre(usuario.getNombre())
                .correo(usuario.getEmail())
                .tipo(usuario instanceof Admin ? "admin" : "usuario")
                .build();
    }

    @Override
    public UsuarioResponseDto actualizarUsuario(String id, UsuarioDto usuarioDto) {
        Optional<Usuario> usuarioOpcional = usuarioRepository.findById(id);
        if (usuarioOpcional.isEmpty()) {
            throw new UsuarioNoEncontradoException("Usuario con id " + id + " no existe");
        }
        Usuario usuario = usuarioOpcional.get();
        usuario.setUser(usuarioDto.getUser());
        usuario.setNombre(usuarioDto.getNombre());
        usuario.setEmail(usuarioDto.getCorreo());
        usuario.setPassword(usuarioDto.getPassword());
        usuarioRepository.save(usuario); // save para update en Mongo
        return UsuarioResponseDto.builder()
                .user(usuario.getUser())
                .nombre(usuario.getNombre())
                .correo(usuario.getEmail())
                .tipo(usuario instanceof Admin ? "admin" : "usuario")
                .build();
    }

    @Override
    public void borrarUsuario(String id) {
        if (!usuarioRepository.existsById(id)) {
            throw new UsuarioNoEncontradoException("Usuario con id " + id + " no existe");
        }
        usuarioRepository.deleteById(id);
    }

    @Override
    public List<UsuarioResponseDto> buscarUsuarios(String user, String nombre, String correo) {
        List<Usuario> todos = usuarioRepository.findAll();
        return todos.stream()
                .filter(u -> user == null    || u.getUser().toLowerCase().contains(user.toLowerCase()))
                .filter(u -> nombre == null  || u.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .filter(u -> correo == null  || u.getEmail().toLowerCase().contains(correo.toLowerCase()))
                .map(u -> UsuarioResponseDto.builder()
                        .user(u.getUser())
                        .nombre(u.getNombre())
                        .correo(u.getEmail())
                        .tipo(u instanceof Admin ? "admin" : "usuario")
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioResponseDto crearAdmin(UsuarioDto dto) {
        if (usuarioRepository.existsByUser(dto.getUser()) || usuarioRepository.existsByEmail(dto.getCorreo())) {
            throw new UsuarioYaExisteException("Ya existe un usuario (o admin) con ese user y/o correo");
        }
        Admin admin = new Admin();
        admin.setUser(dto.getUser());
        admin.setNombre(dto.getNombre());
        admin.setEmail(dto.getCorreo());
        admin.setPassword(dto.getPassword());
        usuarioRepository.save(admin);
        return UsuarioResponseDto.builder()
                .user(admin.getUser())
                .nombre(admin.getNombre())
                .correo(admin.getEmail())
                .tipo("admin")
                .build();
    }
}
