package com.example.cartasIntercambio.dto;

import com.example.cartasIntercambio.model.Mercado.Demanda;
import com.example.cartasIntercambio.model.Mercado.Oferta;
import com.example.cartasIntercambio.model.Usuario.Usuario;
import com.fasterxml.jackson.annotation.JsonFormat;

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
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date fecha;
    private String descripcion;
    private Demanda demanda;
    private List<Oferta> ofertas = new ArrayList<>();
    private Usuario publicador;
    private String estado; // TODO: poner enum EstadoPublicacion y en clase Publicacion

}
