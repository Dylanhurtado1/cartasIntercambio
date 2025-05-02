package com.example.cartasIntercambio.repository.irepository;

import com.example.cartasIntercambio.model.Usuario.Usuario;

import java.util.List;
import java.util.Optional;

public interface IUsuarioRepository {

    void save(Usuario usuario);
    List<Usuario> findAll();
    Optional<Usuario> findById(Long id);
    boolean existsByUser(String user);
    boolean existsByCorreo(String correo);
    void update(Usuario usuario);
    void deleteById(Long id);
}
