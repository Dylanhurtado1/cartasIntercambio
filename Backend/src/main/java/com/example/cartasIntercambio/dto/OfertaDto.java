package com.example.cartasIntercambio.dto;

import com.example.cartasIntercambio.model.Producto_Carta.Carta;
import com.example.cartasIntercambio.model.Usuario.Usuario;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfertaDto {
    private Long id;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date fecha;
    //private Publicacion publicacion;

    private Long idPublicacion;
    private Double monto;
    private List<Carta> cartasOfrecidas;
    private Usuario ofertante;
    private String estado;

  public OfertaDto(Date fecha, /*Publicacion publicacion*/ Long idPublicacion, Double monto, List<Carta> cartasOfrecidas, Usuario ofertante, String estado) {
    this.fecha = fecha;
    //this.publicacion = publicacion;
    this.idPublicacion = idPublicacion;
    this.monto = monto;
    this.cartasOfrecidas = cartasOfrecidas;
    this.ofertante = ofertante;
    this.estado = estado;
  }
}
