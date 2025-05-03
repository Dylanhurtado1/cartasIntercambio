package com.example.cartasIntercambio.repository.irepository;

import com.example.cartasIntercambio.model.Mercado.Publicacion;

import java.util.List;
import java.util.Optional;

public interface IPublicacionRepository{

  void save(Publicacion publicacion);

  List<Publicacion> findAll();

  Optional<Publicacion> findById(Long id);

  List<Publicacion> findByPublicadorId(Long idUser);

  void finalizarPublicacion(Long idPublicacion);

}
