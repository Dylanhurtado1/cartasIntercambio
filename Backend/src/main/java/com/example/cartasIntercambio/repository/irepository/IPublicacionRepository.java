package com.example.cartasIntercambio.repository.irepository;

import com.example.cartasIntercambio.model.Mercado.Publicacion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


public interface IPublicacionRepository extends MongoRepository<Publicacion, Long> {

  List<Publicacion> findAll();

  Optional<Publicacion> findById(Long id);

  List<Publicacion> findByPublicadorId(Long idUser);

//  void finalizarPublicacion(Long idPublicacion);

}
