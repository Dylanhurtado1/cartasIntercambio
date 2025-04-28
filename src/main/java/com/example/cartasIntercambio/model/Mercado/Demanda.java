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

    private Carta cartaOfrecida;
    private Float precio;
    private List<Carta> cartasInteres;
}
