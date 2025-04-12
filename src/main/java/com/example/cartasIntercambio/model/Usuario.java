package com.example.cartasIntercambio.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {

    private String user;
    private String nombre;
    private String apellido;
    private String email;
    private Date fechaNacimiento;

}
