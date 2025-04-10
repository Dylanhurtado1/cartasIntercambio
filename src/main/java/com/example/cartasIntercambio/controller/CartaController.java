package com.example.cartasIntercambio.controller;

import com.example.cartasIntercambio.dto.CartaDto;
import com.example.cartasIntercambio.service.CartaServiceImpl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cartas")
public class CartaController {

  private final CartaServiceImpl cartaService;

  @Autowired
  public CartaController(CartaServiceImpl cartaService) {
    this.cartaService = cartaService;
  }

  @PostMapping
  public ResponseEntity<CartaDto> crearCarta(@RequestBody CartaDto cartaDTO) {
    cartaService.guardarCarta(cartaDTO);
    return new ResponseEntity<>(cartaDTO, HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<CartaDto>> listarCartas() {
    List<CartaDto> cartas = cartaService.listarCartas();
    return new ResponseEntity<>(cartas, HttpStatus.OK);
  }

  @GetMapping("/nombre/{nombre}")
  public ResponseEntity<List<CartaDto>> buscarCartaPorNombre(@PathVariable String nombre) {
    return new ResponseEntity<>(cartaService.buscarCartaPorNombre(nombre), HttpStatus.OK);
  }

  @GetMapping("/juego/{juego}")
  public ResponseEntity<List<CartaDto>> buscarCartaPorJuego(@PathVariable String juego) {
    return new ResponseEntity<>(cartaService.buscarCartaPorJuego(juego), HttpStatus.OK);
  }

  @GetMapping("/estado/{estado}")
  public ResponseEntity<List<CartaDto>> buscarCartaPorEstado(@PathVariable String estado) {
    return new ResponseEntity<>(cartaService.buscarCartaPorEstado(estado), HttpStatus.OK);
  }



}
