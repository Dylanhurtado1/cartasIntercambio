package com.example.cartasIntercambio.service;

import com.example.cartasIntercambio.dto.UsuarioDto;
import com.example.cartasIntercambio.dto.UsuarioResponseDto;
import com.example.cartasIntercambio.model.Usuario.Usuario;

import java.util.List;

public interface IUsuarioService {

    UsuarioResponseDto registrarUsuario(UsuarioDto usuarioDto);
    List<UsuarioResponseDto> listarUsuarios();
    UsuarioResponseDto buscarUsuarioPorId(String id);
    UsuarioResponseDto actualizarUsuario(String id, UsuarioDto usuarioDto);
    void borrarUsuario(String id);
    List<UsuarioResponseDto> buscarUsuarios(String user, String nombre, String correo);
    UsuarioResponseDto crearAdmin(UsuarioDto dto);
}
