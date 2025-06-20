package com.example.cartasIntercambio.dto;

import com.example.cartasIntercambio.model.Mercado.Oferta;
import com.example.cartasIntercambio.model.Producto_Carta.Carta;
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
public class OfertaDto {
    private String id;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date fecha;

    private String idPublicacion;
    private BigDecimal monto;
    private List<Carta> cartasOfrecidas;
    private Usuario ofertante;
    //private Long idOfertante;
    private String estado;
    private Long version;

  public OfertaDto(Date fecha, String idPublicacion, BigDecimal monto, List<Carta> cartasOfrecidas, Usuario ofertante, String estado) {
    this.fecha = fecha;
    this.idPublicacion = idPublicacion;
    this.monto = monto;
    this.cartasOfrecidas = cartasOfrecidas;
    this.ofertante = ofertante;
    this.estado = estado;
  }

  public OfertaDto(Oferta oferta) {
      this.id = oferta.getId();
      this.fecha = oferta.getFecha();
      this.idPublicacion = oferta.getIdPublicacion();
      this.monto = oferta.getMonto();
      this.cartasOfrecidas = oferta.getCartasOfrecidas();
      this.ofertante = oferta.getOfertante();
      this.estado = oferta.getEstado().toString();
      this.version = oferta.getVersion();
  }

}
