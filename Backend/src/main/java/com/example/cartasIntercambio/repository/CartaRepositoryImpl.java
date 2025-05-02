package com.example.cartasIntercambio.repository;

import com.example.cartasIntercambio.model.Producto_Carta.Carta;
import java.util.ArrayList;
import java.util.List;

import com.example.cartasIntercambio.repository.irepository.ICartaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class CartaRepositoryImpl implements ICartaRepository {

  private List<Carta> cartas = new ArrayList<>();

  @Override
  public void save(Carta carta) {
    cartas.add(carta);
  }

  @Override
  public List<Carta> findAll() {
    return cartas;
  }

//  @Override
//  public List<Carta> findByNombre(String nombre) {
//    return cartas.stream().filter(carta -> carta.getNombre().equalsIgnoreCase(nombre)).toList();
//  }
//
//  @Override
//  public List<Carta> findByJuego(String juego) {
//    return cartas.stream().filter(carta -> carta.getJuego().equalsIgnoreCase(juego)).toList();
//  }
//
//  @Override
//  public List<Carta> findByEstado(String estado) {
//    return cartas.stream().filter(carta -> carta.getEstado().equalsIgnoreCase(estado)).toList();
//  }
}
