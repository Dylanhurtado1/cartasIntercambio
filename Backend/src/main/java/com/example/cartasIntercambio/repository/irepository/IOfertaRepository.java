package com.example.cartasIntercambio.repository.irepository;

import com.example.cartasIntercambio.model.Mercado.Oferta;
import com.example.cartasIntercambio.model.Usuario.Usuario;

import java.util.List;
import java.util.Optional;

public interface IOfertaRepository {
  Optional<Oferta> findById(Long id);
  void save(Oferta oferta);
  List<Oferta> findAll();
  List<Oferta> findByOferente(Usuario oferente);
  List<Oferta> findByPublicacion(Long idPublicacion);
  void actualizarOferta(Oferta oferta);
  void rechazarOtrasOfertas(Long idOferta, Long idPublicacion);
}
