package com.example.cartasIntercambio.repository.irepository;

import com.example.cartasIntercambio.model.Producto_Carta.Carta;
import java.util.List;

public interface ICartaRepository {
  void save(Carta carta);
  List<Carta> findAll();
}
