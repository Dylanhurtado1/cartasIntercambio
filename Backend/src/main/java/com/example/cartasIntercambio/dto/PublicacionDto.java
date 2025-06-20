package com.example.cartasIntercambio.dto;

import com.example.cartasIntercambio.model.Mercado.Publicacion;
import com.example.cartasIntercambio.model.Producto_Carta.Carta;
import com.example.cartasIntercambio.model.Usuario.Usuario;
import com.fasterxml.jackson.annotation.JsonFormat;

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
public class PublicacionDto {
    private String id; //TODO: Creo que no van los id en los DTOs, por ahi me equivoco
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date fecha;
    private String descripcion;
    private CartaDto cartaOfrecida;
    private BigDecimal precio;
    private List<CartaDto> cartasInteres;
    private Usuario publicador;
    //private Long idPublicador;
    private String estado;
    private Long version;


    // Constructor desde entidad, ajustá si la entidad usa otra clase
    public PublicacionDto(Publicacion publicacion) {
        this.id = publicacion.getId();
        this.fecha = publicacion.getFecha();
        this.descripcion = publicacion.getDescripcion();
        // Convertí a CartaDto si tu entidad usa Carta real:
        this.cartaOfrecida = cartaToDto(publicacion.getCartaOfrecida());
        this.precio = publicacion.getPrecio();
        this.cartasInteres = cartaListToDto(publicacion.getCartasInteres());
        this.publicador = publicacion.getPublicador();
        this.estado = publicacion.getEstado().toString();
    }

    // Métodos auxiliares para la conversión
    private CartaDto cartaToDto(Carta carta) {
        return new CartaDto(
                carta.getJuego(),
                carta.getNombre(),
                carta.getEstado().toString(),
                carta.getImagenes() // suponiendo que ahora Imagenes es una lista de Strings (URLs)
        );
    }

    private List<CartaDto> cartaListToDto(List<Carta> lista) {
        if (lista == null) return null;
        List<CartaDto> out = new ArrayList<>();
        for (Carta c : lista) out.add(cartaToDto(c));
        return out;
    }
}
