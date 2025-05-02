package com.example.cartasIntercambio.service;

import com.example.cartasIntercambio.dto.OfertaDto;
import com.example.cartasIntercambio.model.Mercado.EstadoOferta;
import com.example.cartasIntercambio.model.Mercado.Oferta;
import com.example.cartasIntercambio.repository.OfertaRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OfertaServiceImpl implements IOfertaService{

    private final OfertaRepositoryImpl ofertaRepository;

    @Autowired
    public OfertaServiceImpl(OfertaRepositoryImpl ofertaRepository) {
        this.ofertaRepository = ofertaRepository;
    }

    @Override
    public Optional<Oferta> buscarOferta(Long idOferta) {
        return ofertaRepository.findById(idOferta);
    }

    @Override
    public void crearOferta(OfertaDto ofertaDto, Long idPublicacion) {
        Oferta nuevaOferta = new Oferta(
                ofertaDto.getFecha(),
                idPublicacion,
                ofertaDto.getMonto(),
                ofertaDto.getCartasOfrecidas(),
                ofertaDto.getOfertante(),
                EstadoOferta.valueOf("PENDIENTE")
        );
        ofertaRepository.save(nuevaOferta);
    }
}
