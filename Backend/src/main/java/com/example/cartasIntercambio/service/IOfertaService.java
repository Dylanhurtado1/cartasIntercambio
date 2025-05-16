package com.example.cartasIntercambio.service;

import com.example.cartasIntercambio.dto.OfertaDto;
import com.example.cartasIntercambio.model.Mercado.Oferta;
import com.example.cartasIntercambio.model.Mercado.Publicacion;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IOfertaService {

    OfertaDto buscarOfertaDto(String idOferta);

    void crearOferta(OfertaDto ofertaDto, Publicacion publicacion);

    List<OfertaDto> buscarOfertasPorPublicacion(Publicacion publicacion, String idUsuario);

    List<OfertaDto> buscarOfertasPorPublicacion(String idPublicacion);

    void guardarOferta(Oferta oferta);

    void rechazarOtrasOfertas(String idOferta, String idPublicacion);

    List<OfertaDto> buscarOfertasRealizadas(String idUsuario);

}
