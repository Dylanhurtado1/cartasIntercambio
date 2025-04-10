package com.example.cartasIntercambio.repository;

import com.example.cartasIntercambio.model.Carta;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class CartaRepositoryImpl implements ICartaRepository{

  private List<Carta> cartas = new ArrayList<>();

  @Override
  public void save(Carta carta) {
    cartas.add(carta);
  }

  @Override
  public List<Carta> findAll() {
    return cartas;
  }
}
