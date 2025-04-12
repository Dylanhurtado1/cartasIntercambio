package com.example.cartasIntercambio.model;

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
  private String estado;
  private List<String> imagenes;


}
