package com.example.cartasIntercambio.repository.irepository;

import com.example.cartasIntercambio.model.Mercado.Oferta;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface IOfertaRepository extends MongoRepository<Oferta, String> {

  /*List<Oferta> findByOfertante(Long idOfertante);
  List<Oferta> findByPublicacion(String idPublicacion);
  void actualizarOferta(Oferta oferta);
  void rechazarOtrasOfertas(Long idOferta, String idPublicacion);*/
  List<Oferta> findByOfertante(String idOfertante);        // Si el idOfertante es String
  List<Oferta> findByIdPublicacion(String idPublicacion);
}
