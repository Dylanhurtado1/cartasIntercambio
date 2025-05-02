package com.example.cartasIntercambio.model.Mercado;

import com.example.cartasIntercambio.model.Producto_Carta.Carta;
import com.example.cartasIntercambio.model.Producto_Carta.EstadoCarta;
import com.example.cartasIntercambio.model.Usuario.Usuario;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Oferta {

  private Long id;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private Date fecha;  
  private Long idPublicacion;
  private BigDecimal monto;
  private List<Carta> cartasOfrecidas;
  private Usuario ofertante;
//  private Long idOfertante;
  private EstadoOferta estado;

  public Oferta(Date fecha, Long idPublicacion, BigDecimal monto, List<Carta> cartasOfrecidas, Usuario ofertante, EstadoOferta estado) {
    this.fecha = fecha;
    this.idPublicacion = idPublicacion;
    this.monto = monto;
    this.cartasOfrecidas = cartasOfrecidas;
    this.ofertante = ofertante;
    this.estado = estado;
  }
}
