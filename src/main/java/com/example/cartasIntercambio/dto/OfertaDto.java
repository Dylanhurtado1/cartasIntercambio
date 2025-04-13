package com.example.cartasIntercambio.dto;

import com.example.cartasIntercambio.model.Mercado.Publicacion;
import com.example.cartasIntercambio.model.Producto_Carta.Carta;
import com.example.cartasIntercambio.model.Usuario.Usuario;

import java.util.Date;
import java.util.List;

public class OfertaDto {
    private Date fecha;
    private Publicacion publicacion;
    private Double monto;
    private List<Carta> cartasOfrecidas;
    private Usuario ofertante;
    private String estado;

}
