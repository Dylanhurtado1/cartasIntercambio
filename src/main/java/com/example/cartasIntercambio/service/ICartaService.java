package com.example.cartasIntercambio.service;

import com.example.cartasIntercambio.dto.CartaDto;
import java.util.List;

public interface ICartaService {

  void guardarCarta(CartaDto cartaDTO);
  List<CartaDto> listarCartas();

}
