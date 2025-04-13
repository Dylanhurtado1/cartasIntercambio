package com.example.cartasIntercambio.repository;

import com.example.cartasIntercambio.model.Mercado.Oferta;
import com.example.cartasIntercambio.model.Usuario.Usuario;
import com.example.cartasIntercambio.repository.irepository.IOfertaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class OfertaRepositoryImpl implements IOfertaRepository {

  private List<Oferta> ofertas = new ArrayList<>();

  @Override
  public void save(Oferta oferta) {
    ofertas.add(oferta);
  }

  @Override
  public List<Oferta> findAll() {
    return ofertas;
  }

  @Override
  public List<Oferta> findByOferente(Usuario oferente) {
    return ofertas.stream().filter(carta -> carta.getOfertante().equals(oferente)).toList();
  }
}
