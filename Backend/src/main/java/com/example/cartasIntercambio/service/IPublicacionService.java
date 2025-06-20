package com.example.cartasIntercambio.service;

import com.example.cartasIntercambio.dto.PublicacionDto;
import com.example.cartasIntercambio.dto.PublicacionResponse;
import org.bson.Document;

import java.util.List;

public interface IPublicacionService {

    List<PublicacionDto> listarPublicaciones();

    PublicacionResponse buscarPublicacionesFiltradas(String nombre, String juego, String estado,
                                                     Double preciomin, Double preciomax, int pageNo,
                                                     int pageSize);

    List<PublicacionDto> buscarPublicacionesPorUsuario(String idUsuario);

    PublicacionDto guardarPublicacion(PublicacionDto nuevaPublicacionDto);

    //List<OfertaDto> buscarOfertasPorPublicacion(Long idPublicacion, Long idUsuario);

    PublicacionDto buscarPublicacionDTOPorId(String idPublicacion);

    void finalizarPublicacion(String idPublicacion);

    Document contarPublicacionesPorJuego();
}
