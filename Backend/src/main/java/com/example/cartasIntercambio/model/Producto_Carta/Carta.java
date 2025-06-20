package com.example.cartasIntercambio.model.Producto_Carta;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Carta {
  @Field("juego")
  private String juego;
  @Field("nombre")
  private String nombre;
  @Field("estado")
  private EstadoCarta estado;
  private List<String> imagenes;

}
