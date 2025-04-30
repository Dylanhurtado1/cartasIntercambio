package com.example.cartasIntercambio.model.Producto_Carta;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Carta {
  private String nombre;
  private String juego;
  private String estado;//TODO: pasarlo al enum EstadoCarta
  private List<String> imagenes;


}
