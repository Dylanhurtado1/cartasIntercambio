package com.example.cartasIntercambio.model.Mercado;

import com.example.cartasIntercambio.model.Producto_Carta.Carta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Demanda {

    private List<Carta> cartasOfrecidads;
    private String descripcion;
    private Double precio;
    private List<Carta> cartasInteres;
}
