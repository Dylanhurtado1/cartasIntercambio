package com.example.cartasIntercambio.model.Mercado;

import com.example.cartasIntercambio.dto.PublicacionDto;
import com.example.cartasIntercambio.model.Producto_Carta.Carta;
import com.example.cartasIntercambio.model.Usuario.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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
    private Carta cartaOfrecida;
    private BigDecimal precio;
    private List<Carta> cartasInteres;
    //private Long idUsuario; TODO: Cuando haya servicio usuarios
    private Usuario publicador;
    private EstadoPublicacion estado;

}
