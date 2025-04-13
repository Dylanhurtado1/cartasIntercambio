package com.example.cartasIntercambio.service;

import com.example.cartasIntercambio.dto.PublicacionDto;
import com.example.cartasIntercambio.model.Mercado.Publicacion;

import java.util.List;

public interface IPublicacionService {

    //void crearPublicacion(PublicacionDto publicacionDTO); // TODO: punto 1

    //void crearOferta(PublicacionDto publicacionDTO); // TODO: punto 3

    List<PublicacionDto> listarPublicaciones();

    void guardarPublicacion(PublicacionDto nuevaPublicacionDto);

}
