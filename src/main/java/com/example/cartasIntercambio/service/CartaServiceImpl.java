package com.example.cartasIntercambio.service;

import com.example.cartasIntercambio.dto.CartaDto;
import com.example.cartasIntercambio.model.Carta;
import com.example.cartasIntercambio.repository.CartaRepositoryImpl;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartaServiceImpl implements ICartaService {

  private final CartaRepositoryImpl cartaRepositoryImpl;

  @Autowired
  public CartaServiceImpl(CartaRepositoryImpl cartaRepositoryImpl) {
    this.cartaRepositoryImpl = cartaRepositoryImpl;
  }

  @Override
  public void guardarCarta(CartaDto cartaDTO) {
    Carta carta = new Carta(
        cartaDTO.getJuego(),
        cartaDTO.getNombre(),
        cartaDTO.getEstado(),
        cartaDTO.getImagenes(),
        cartaDTO.getValorEstimado(),
        cartaDTO.getCartasInteres()
    );
    cartaRepositoryImpl.save(carta);
  }

  @Override
  public List<CartaDto> listarCartas() {
    List<Carta> cartas = cartaRepositoryImpl.findAll();
    return cartas.stream()
        .map(carta -> new CartaDto(
            carta.getJuego(),
            carta.getNombre(),
            carta.getEstado(),
            carta.getImagenes(),
            carta.getValorEstimado(),
            carta.getCartasInteres()
        ))
        .collect(Collectors.toList());
  }
}
