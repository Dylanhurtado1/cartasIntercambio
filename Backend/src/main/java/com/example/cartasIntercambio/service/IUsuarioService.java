package com.example.cartasIntercambio.service;

import com.example.cartasIntercambio.dto.UsuarioDto;
import com.example.cartasIntercambio.model.Usuario.Usuario;

import java.util.List;

public interface IUsuarioService {

    void registrarUsuario(UsuarioDto usuarioDto);
    List<UsuarioDto> listarUsuarios();

}
