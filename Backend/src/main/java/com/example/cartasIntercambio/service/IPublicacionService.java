package com.example.cartasIntercambio.service;

import com.example.cartasIntercambio.dto.PublicacionDto;
import com.example.cartasIntercambio.dto.PublicacionResponse;
import java.util.List;
import java.util.Map;

public interface IPublicacionService {

    List<PublicacionDto> listarPublicaciones();

    PublicacionResponse buscarPublicacionesFiltradas(String nombre, String juego, String estado,
                                                     Double preciomin, Double preciomax, int pageNo,
                                                     int pageSize);

    PublicacionResponse buscarPublicacionesPorUsuario(String idUsuario, int pageNo, int pageSize);

    List<PublicacionDto> buscarPublicacionesPorUsuario(String idUsuario);

    PublicacionDto guardarPublicacion(PublicacionDto nuevaPublicacionDto);

    //List<OfertaDto> buscarOfertasPorPublicacion(Long idPublicacion, Long idUsuario);

    PublicacionDto buscarPublicacionDTOPorId(String idPublicacion);

    void finalizarPublicacion(String idPublicacion,Long version);

    Map<String, Integer> contarPublicacionesPorJuego();
}
