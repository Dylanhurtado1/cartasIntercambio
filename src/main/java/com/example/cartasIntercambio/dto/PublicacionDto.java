package com.example.cartasIntercambio.dto;

import com.example.cartasIntercambio.model.Mercado.Demanda;
import com.example.cartasIntercambio.model.Mercado.EstadoPublicacion;
import com.example.cartasIntercambio.model.Mercado.Oferta;
import com.example.cartasIntercambio.model.Usuario.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublicacionDto {
    private Date fecha;
    private String descripcion;
    private Demanda demanda;
    private List<Oferta> ofertas = new ArrayList<>();
    private Usuario publicador;
    private String estado; // TODO: poner enum EstadoPublicacion y en clase Publicacion

}
