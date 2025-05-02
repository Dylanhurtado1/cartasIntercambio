package com.example.cartasIntercambio.model.Mercado;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Filtros {

    private String nombreCarta;
    private String nombreJuego;
    private String estado;
    private BigDecimal precioMin;
    private BigDecimal precioMax;
}
