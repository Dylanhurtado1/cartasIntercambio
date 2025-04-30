package com.example.cartasIntercambio.repository.irepository;

import com.example.cartasIntercambio.model.Producto_Carta.Carta;
import java.util.List;

public interface ICartaRepository {
  void save(Carta carta);
  List<Carta> findAll();
  List<Carta> findByNombre(String nombre);
  List<Carta> findByJuego(String juego);
  List<Carta> findByEstado(String juego);
}
