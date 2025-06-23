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
    List<UsuarioResponseDto> buscarUsuarios(String user, String nombre, String correo);
    UsuarioResponseDto actualizarUsuario(String id, UsuarioDto usuarioDto);
    List<UsuarioResponseDto> listarUsuarios();
    UsuarioResponseDto crearAdmin(UsuarioDto dto);
    void borrarUsuario(String id);







}
