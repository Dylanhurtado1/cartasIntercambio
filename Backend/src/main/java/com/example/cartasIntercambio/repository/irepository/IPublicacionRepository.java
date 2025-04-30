package com.example.cartasIntercambio.repository.irepository;

import com.example.cartasIntercambio.model.Mercado.Publicacion;
import com.example.cartasIntercambio.model.Usuario.Usuario;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface IPublicacionRepository {

  void save(Publicacion publicacion);

  List<Publicacion> findAll();

  List<Publicacion> findByPublicadorId(Long idUser);

  Optional<Publicacion> findById(Long id);

  List<Publicacion> findByCardName(String name);

  List<Publicacion> findByGameName(String game);

  List<Publicacion> findByCardState(String state);

  List<Publicacion> findByCost(BigDecimal precio);

}
