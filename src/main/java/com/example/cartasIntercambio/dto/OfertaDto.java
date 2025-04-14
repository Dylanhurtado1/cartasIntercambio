package com.example.cartasIntercambio.dto;

import com.example.cartasIntercambio.model.Mercado.Publicacion;
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
public class OfertaDto {
    private Long id;
    private Date fecha;
    private Publicacion publicacion;
    private Double monto;
    private List<Carta> cartasOfrecidas;
    private Usuario ofertante;
    private String estado;

    public OfertaDto(Date fecha, Publicacion publicacion, Double monto, List<Carta> cartasOfrecidas, Usuario ofertante, String estado) {
        this.fecha = fecha;
        this.publicacion = publicacion;
        this.monto = monto;
        this.cartasOfrecidas = cartasOfrecidas;
        this.ofertante = ofertante;
        this.estado = estado;
    }
}
