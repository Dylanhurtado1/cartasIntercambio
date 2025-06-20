package com.example.cartasIntercambio.model.Mercado;

import com.example.cartasIntercambio.model.Producto_Carta.Carta;
import com.example.cartasIntercambio.model.Usuario.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Publicacion")
public class Publicacion {
    @Id
    private String id;
    private Date fecha;
    private String descripcion;
    @Field("cartaOfrecida")
    private Carta cartaOfrecida;
    @Field(targetType = FieldType.DOUBLE, name = "precio")
    private BigDecimal precio;
    private List<Carta> cartasInteres;
    //private Long idUsuario; TODO: Cuando haya servicio usuarios
    private Usuario publicador;
    private EstadoPublicacion estado;
    @Version
    private Long version;


}
