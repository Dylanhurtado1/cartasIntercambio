package com.example.cartasIntercambio.repository.irepository;

import com.example.cartasIntercambio.model.Mercado.Oferta;
import com.example.cartasIntercambio.model.Usuario.Usuario;

import java.util.List;

public interface IOfertaRepository {
  void save(Oferta oferta);
  List<Oferta> findAll();
  List<Oferta> findByOferente(Usuario oferente);
}
