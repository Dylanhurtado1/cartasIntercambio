package com.example.cartasIntercambio.repository.irepository;

import com.example.cartasIntercambio.model.Mercado.Publicacion;
import com.example.cartasIntercambio.model.Usuario.Usuario;

import java.util.List;

public interface IPublicacionRepository {
  void save(Publicacion publicacion);
  List<Publicacion> findAll();
  List<Publicacion> findByPublicador(Usuario publicador);
}
