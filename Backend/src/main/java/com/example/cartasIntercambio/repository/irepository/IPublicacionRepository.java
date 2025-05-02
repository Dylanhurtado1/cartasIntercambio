package com.example.cartasIntercambio.repository.irepository;

import com.example.cartasIntercambio.model.Mercado.Publicacion;
import com.example.cartasIntercambio.model.Usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface IPublicacionRepository{

  void save(Publicacion publicacion);

  List<Publicacion> findAll();

  Optional<Publicacion> findById(Long id);

  List<Publicacion> findByPublicadorId(Long idUser);

//  List<Publicacion> findByCardName(String name);
//
//  List<Publicacion> findByGameName(String game);
//
//  List<Publicacion> findByCardState(String state);
//
//  List<Publicacion> findByCost(BigDecimal precio);

}
