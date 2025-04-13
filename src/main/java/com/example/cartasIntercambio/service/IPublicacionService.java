package com.example.cartasIntercambio.service;

import com.example.cartasIntercambio.dto.OfertaDto;
import com.example.cartasIntercambio.dto.PublicacionDto;

import java.util.List;

public interface IPublicacionService {

    List<PublicacionDto> listarPublicaciones();

    void guardarPublicacion(PublicacionDto nuevaPublicacionDto);

    void crearOferta(Long idPublicacion, OfertaDto ofertaDto);

}
