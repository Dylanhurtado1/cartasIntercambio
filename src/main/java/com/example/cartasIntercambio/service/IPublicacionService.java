package com.example.cartasIntercambio.service;

import com.example.cartasIntercambio.dto.OfertaDto;
import com.example.cartasIntercambio.dto.PublicacionDto;
import com.example.cartasIntercambio.model.Mercado.Publicacion;

import java.util.List;

public interface IPublicacionService {

    Publicacion buscarPublicacionPorId(Long idPublicacion);

    List<PublicacionDto> buscarPublicacionesPorUsuario(Long idUsuario);

    void guardarPublicacion(PublicacionDto nuevaPublicacionDto);

    void crearOferta(Long idPublicacion, OfertaDto ofertaDto);

    List<PublicacionDto> buscarPublicacionPorNombre(String nombre);

    List<PublicacionDto> buscarPublicacionPorJuego(String juego);

    List<PublicacionDto> buscarPublicacionPorEstadoDeCarta(String estado);

    List<PublicacionDto> buscarPublicacionPorPrecio(Float precio);

    List<OfertaDto> buscarOfertasPorPublicacion(Long idPublicacion, Long idUsuario);

}
