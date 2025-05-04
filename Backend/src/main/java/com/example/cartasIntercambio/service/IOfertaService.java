package com.example.cartasIntercambio.service;

import com.example.cartasIntercambio.dto.OfertaDto;
import com.example.cartasIntercambio.model.Mercado.Oferta;
import com.example.cartasIntercambio.model.Mercado.Publicacion;

import java.util.List;

public interface IOfertaService {

    OfertaDto buscarOfertaDto(Long idOferta);

    void crearOferta(OfertaDto ofertaDto, Publicacion publicacion);

    List<OfertaDto> buscarOfertasPorPublicacion(Publicacion publicacion, Long idUsuario);

    List<OfertaDto> buscarOfertasPorPublicacion(Long idPublicacion);

    void guardarOferta(Oferta oferta);

    void rechazarOtrasOfertas(Long idOferta, Long idPublicacion);

    List<OfertaDto> buscarOfertasRealizadas(Long idUsuario);

}
