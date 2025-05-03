package com.example.cartasIntercambio.service;

import com.example.cartasIntercambio.dto.CartaDto;
import com.example.cartasIntercambio.model.Producto_Carta.Carta;
import com.example.cartasIntercambio.model.Producto_Carta.EstadoCarta;
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
        EstadoCarta.valueOf(cartaDTO.getEstado()),
        cartaDTO.getImagenes()
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
            carta.getEstado().toString(),
            carta.getImagenes()
        ))
        .collect(Collectors.toList());
  }

//  @Override
//  public List<CartaDto> buscarCartaPorNombre(String nombre) {
//    List<Carta> cartas = cartaRepositoryImpl.findByNombre(nombre);
//    if(cartas.isEmpty()){
//      return new ArrayList<>();
//    }
//    return cartas.stream().map(carta -> new CartaDto(
//        carta.getJuego().toString(),
//        carta.getNombre(),
//        carta.getEstado().toString(),
//        carta.getImagenes()
//    )).collect(Collectors.toList());
//  }
//
//  @Override
//  public List<CartaDto> buscarCartaPorJuego(String juego) {
//    List<Carta> cartas = cartaRepositoryImpl.findByJuego(juego);
//    if(cartas.isEmpty()){
//      return new ArrayList<>();
//    }
//    return cartas.stream().map(carta -> new CartaDto(
//        carta.getJuego().toString(),
//        carta.getNombre(),
//        carta.getEstado().toString(),
//        carta.getImagenes()
//    )).collect(Collectors.toList());
//  }
//
//  @Override
//  public List<CartaDto> buscarCartaPorEstado(String estado) {
//    List<Carta> cartas = cartaRepositoryImpl.findByEstado(estado);
//    if(cartas.isEmpty()){
//      return new ArrayList<>();
//    }
//    return cartas.stream().map(carta -> new CartaDto(
//        carta.getJuego().toString(),
//        carta.getNombre(),
//        carta.getEstado().toString(),
//        carta.getImagenes()
//    )).collect(Collectors.toList());
//  }

}
