package com.example.cartasIntercambio.service;

import com.example.cartasIntercambio.dto.UsuarioDto;
import com.example.cartasIntercambio.dto.UsuarioResponseDto;
import com.example.cartasIntercambio.exception.UsuarioNoEncontradoException;
import com.example.cartasIntercambio.exception.UsuarioYaExisteException;
import com.example.cartasIntercambio.jwt.JwtUtil;
import com.example.cartasIntercambio.model.Usuario.Admin;
import com.example.cartasIntercambio.model.Usuario.Usuario;
import com.example.cartasIntercambio.repository.irepository.IUsuarioRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioServiceImpl implements IUsuarioService {

    private final IUsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;
    private final CookieService cookieService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioServiceImpl(IUsuarioRepository usuarioRepository, JwtUtil jwtUtil, CookieService cookieService, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.jwtUtil = jwtUtil;
        this.cookieService = cookieService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UsuarioResponseDto registrarUsuario(UsuarioDto usuarioDto) {
        System.out.println(usuarioDto);
        if (usuarioRepository.existsByUser(usuarioDto.getUser()) || usuarioRepository.existsByEmail(usuarioDto.getCorreo())) {
            throw new UsuarioYaExisteException("Ya existe un usuario con ese user y/o correo");
        }
        Usuario usuario = new Usuario();
        usuario.setUser(usuarioDto.getUser());
        usuario.setNombre(usuarioDto.getNombre());
        usuario.setEmail(usuarioDto.getCorreo());
        usuario.setPassword(passwordEncoder.encode(usuarioDto.getPassword()));
        Usuario saved = usuarioRepository.save(usuario);
        return UsuarioResponseDto.builder()
                .user(saved.getUser())
                .nombre(saved.getNombre())
                .correo(saved.getEmail())
                .tipo("USER")
                .id(saved.getId())
                .build();
    }

    @Override
    public UsuarioResponseDto buscarUsuarioPorId(String id) {
        Optional<Usuario> usuarioOpcional = usuarioRepository.findById(id);
        if (usuarioOpcional.isEmpty()) {
            throw new UsuarioNoEncontradoException("Usuario con id " + id + " no existe");
        }
        Usuario usuario = usuarioOpcional.get();
        return UsuarioResponseDto.builder()
                .id(usuario.getId())
                .user(usuario.getUser())
                .nombre(usuario.getNombre())
                .correo(usuario.getEmail())
                .tipo(usuario instanceof Admin ? "admin" : "usuario")
                .build();
    }

    @Override
    public UsuarioResponseDto login(UsuarioDto userLogin, HttpServletResponse response) {

        Optional<Usuario> usuarioOpt = usuarioRepository.findByUser(userLogin.getUser());
        if(usuarioOpt.isEmpty()) return null;
        Usuario usuario = usuarioOpt.get();
        if(!passwordEncoder.matches(userLogin.getPassword(), usuario.getPassword())) return null;

        Usuario usuarioDB = usuarioOpt.get();


        String jwt = jwtUtil.generateToken(usuarioDB.getId(), usuarioDB.getUser());
        cookieService.addHttpCookie("jwt", jwt, 60*60, response);

        return UsuarioResponseDto.builder()
                .id(usuarioDB.getId())
                .user(usuarioDB.getUser())
                .nombre(usuarioDB.getNombre())
                .correo(usuarioDB.getEmail())
                .tipo(usuarioDB instanceof Admin ? "admin" : "usuario")
                .build();
    }

    @Override
    public void logout(HttpServletResponse response) {
        cookieService.deleteHttpCookie("jwt", response);
    }

}