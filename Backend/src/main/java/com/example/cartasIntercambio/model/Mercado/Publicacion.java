package com.example.cartasIntercambio.model.Mercado;

import com.example.cartasIntercambio.dto.PublicacionDto;
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
public class Publicacion {
    private Long id;
    private Date fecha;
    private String descripcion;
    private Demanda demanda;
    private List<Oferta> ofertas = new ArrayList<>();
    private Usuario publicador;
    private String estado;

    public void agregarOferta(Oferta oferta) {
        ofertas.add(oferta);
    }

}
