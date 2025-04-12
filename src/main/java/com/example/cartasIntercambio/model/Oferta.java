package com.example.cartasIntercambio.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Oferta {
  private Date fecha;
  private Publicacion publicacion;
  private Double monto;
  private Usuario oferente;
  private String estado;
}
