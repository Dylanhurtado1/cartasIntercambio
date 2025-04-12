package com.example.cartasIntercambio.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Publicacion {
    private Date fecha;
    private Demanda demanda;
    private List<Oferta> ofertas;
    private Usuario dueno;
    private String estado;

}
