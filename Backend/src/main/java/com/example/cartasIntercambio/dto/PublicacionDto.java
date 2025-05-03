package com.example.cartasIntercambio.dto;

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
public class PublicacionDto {
    private Long id; //TODO: Creo que no van los id en los DTOs, por ahi me equivoco
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date fecha;
    private String descripcion;
    private Carta cartaOfrecida;
    private BigDecimal precio;
    private List<Carta> cartasInteres;
    private Usuario publicador;
    //private Long idPublicador;
    private String estado;

}
