package com.example.cartasIntercambio.service;

import com.example.cartasIntercambio.dto.UsuarioDto;
import com.example.cartasIntercambio.dto.UsuarioResponseDto;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface IUsuarioService {

    UsuarioResponseDto registrarUsuario(UsuarioDto usuarioDto);
    UsuarioResponseDto login(UsuarioDto usuarioDto, HttpServletResponse response);
    void logout(HttpServletResponse response);
    UsuarioResponseDto buscarUsuarioPorId(String id);

}
