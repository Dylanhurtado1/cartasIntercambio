package com.example.cartasIntercambio.service;

import com.example.cartasIntercambio.dto.OfertaDto;
import com.example.cartasIntercambio.dto.PublicacionDto;
import com.example.cartasIntercambio.model.Mercado.Oferta;

import java.util.Optional;

public interface IOfertaService {

    Optional<Oferta> buscarOferta(Long idOferta);

    void crearOferta(OfertaDto ofertaDto, Long idPublicacion);

}
