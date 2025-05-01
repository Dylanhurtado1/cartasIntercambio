package com.example.cartasIntercambio.repository.irepository;

import com.example.cartasIntercambio.model.Usuario.Usuario;

import java.util.List;

public interface IUsuarioRepository {

    void save(Usuario usuario);
    List<Usuario> findAll();
    //Usuario findById(Long id);                     // Buscar un usuario por su ID
    //Usuario findByEmail(String email);
}
