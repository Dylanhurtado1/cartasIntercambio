package com.example.cartasIntercambio.model.Mercado;

import com.example.cartasIntercambio.model.Producto_Carta.Carta;
import com.example.cartasIntercambio.model.Usuario.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Oferta {
  private Date fecha;
  private Publicacion publicacion;
  private Double monto;
  private List<Carta> cartasOfrecidas;
  private Usuario ofertante;
  private String estado;
}
