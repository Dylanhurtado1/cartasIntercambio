package com.example.cartasIntercambio.service;

import com.example.cartasIntercambio.dto.UsuarioDto;
import com.example.cartasIntercambio.dto.UsuarioResponseDto;
import com.example.cartasIntercambio.model.Usuario.Usuario;

import java.util.List;

public interface IUsuarioService {

    void registrarUsuario(UsuarioDto usuarioDto);
    List<UsuarioResponseDto> listarUsuarios();
    UsuarioResponseDto buscarUsuarioPorId(Long id);
    UsuarioResponseDto actualizarUsuario(Long id, UsuarioDto usuarioDto);
    void borrarUsuario(Long id);
    List<UsuarioResponseDto> buscarUsuarios(String user, String nombre, String correo);
    UsuarioResponseDto crearAdmin(UsuarioDto dto);
}
