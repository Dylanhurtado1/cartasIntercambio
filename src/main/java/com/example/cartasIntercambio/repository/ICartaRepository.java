package com.example.cartasIntercambio.repository;

import com.example.cartasIntercambio.model.Carta;
import java.util.List;

public interface ICartaRepository {
  void save(Carta carta);
  List<Carta> findAll();
  List<Carta> findByNombre(String nombre);
  List<Carta> findByJuego(String juego);
  List<Carta> findByEstado(String juego);
}
