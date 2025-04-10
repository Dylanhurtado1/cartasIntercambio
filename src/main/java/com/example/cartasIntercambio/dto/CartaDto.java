package com.example.cartasIntercambio.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartaDto {
  private String juego;
  private String nombre;
  private String estado;
  private List<String> imagenes;
  private Double valorEstimado;
  private List<String> cartasInteres;
}
