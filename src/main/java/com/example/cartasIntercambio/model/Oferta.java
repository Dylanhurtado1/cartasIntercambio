package com.example.cartasIntercambio.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Oferta {
  private Long id;
  private Long cartaId;
  private String usuarioId;
  private Double monto;
  private Long cartaOfrecidaId;
}
