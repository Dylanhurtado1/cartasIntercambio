package com.example.cartasIntercambio.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartaDto {
  private String juego;
  private String nombre;
  private String estado;
  private List<String> imagenes;
}
