package com.example.cartasIntercambio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResponseDto {
    private String user;
    private String nombre;
    private String correo;
    private String tipo;
}
