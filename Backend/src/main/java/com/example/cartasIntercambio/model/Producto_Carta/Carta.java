package com.example.cartasIntercambio.model.Producto_Carta;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Carta {
  private String juego;
  private String nombre;
  private EstadoCarta estado;
  private List<String> imagenes;


}
