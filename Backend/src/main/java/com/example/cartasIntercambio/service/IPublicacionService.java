package com.example.cartasIntercambio.service;

import com.example.cartasIntercambio.dto.PublicacionDto;
import java.util.List;

public interface IPublicacionService {

    List<PublicacionDto> listarPublicaciones();

    List<PublicacionDto> buscarPublicacionesPorUsuario(Long idUsuario);

    void guardarPublicacion(PublicacionDto nuevaPublicacionDto);

    //List<OfertaDto> buscarOfertasPorPublicacion(Long idPublicacion, Long idUsuario);

    PublicacionDto buscarPublicacionDTOPorId(Long idPublicacion);

    void finalizarPublicacion(Long idPublicacion);

}
