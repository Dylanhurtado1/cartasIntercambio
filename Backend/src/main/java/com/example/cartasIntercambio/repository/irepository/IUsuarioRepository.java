package com.example.cartasIntercambio.repository.irepository;

import com.example.cartasIntercambio.model.Usuario.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface IUsuarioRepository extends MongoRepository<Usuario, String> {
    Optional<Usuario> findByUser(String user);
    boolean existsByUser(String user);
    boolean existsByCorreo(String correo);
    /*void save(Usuario usuario);
    List<Usuario> findAll();
    Optional<Usuario> findById(String id); // Cambiado a String
    boolean existsByUser(String user);
    boolean existsByCorreo(String correo);
    void update(Usuario usuario);
    void deleteById(String id); // Cambiado a String */
}
