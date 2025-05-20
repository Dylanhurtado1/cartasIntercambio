package com.example.cartasIntercambio.repository.irepository;

import com.example.cartasIntercambio.model.Usuario.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;


public interface IUsuarioRepository extends MongoRepository<Usuario, String> {

    boolean existsByUser(String user);
    boolean existsByEmail(String correo);
    Optional<Usuario> findByUser(String user);
}
