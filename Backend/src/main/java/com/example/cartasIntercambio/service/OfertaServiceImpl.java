package com.example.cartasIntercambio.service;

import com.example.cartasIntercambio.dto.OfertaDto;
import com.example.cartasIntercambio.model.Mercado.EstadoOferta;
import com.example.cartasIntercambio.model.Mercado.EstadoPublicacion;
import com.example.cartasIntercambio.model.Mercado.Oferta;
import com.example.cartasIntercambio.model.Mercado.Publicacion;
import com.example.cartasIntercambio.repository.OfertaRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OfertaServiceImpl implements IOfertaService{
    private final OfertaRepositoryImpl ofertaRepository;

    @Autowired
    public OfertaServiceImpl(OfertaRepositoryImpl ofertaRepository) {
        this.ofertaRepository = ofertaRepository;
    }

    @Override
    public OfertaDto buscarOfertaDto(Long idOferta) {
        Oferta oferta = buscarOfertaPorId(idOferta);

        return new OfertaDto(
                oferta.getFecha(),
                oferta.getIdPublicacion(),
                oferta.getMonto(),
                oferta.getCartasOfrecidas(),
                oferta.getOfertante(),
                oferta.getEstado().toString()
        );
    }

    public Oferta buscarOfertaPorId(Long idOferta) {

        return ofertaRepository.findById(idOferta)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la oferta con id: " + idOferta));
    }

    @Override
    public void crearOferta(OfertaDto ofertaDto, Publicacion publicacion) {
        if(!publicacion.getEstado().equals(EstadoPublicacion.ACTIVA)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La publicación ya no está activa.");
        }

        /*if(publicacion.getPublicador().getId().equals(ofertaDto.getOfertante().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No es posible ofertar una publicacion propia.");
        }*/

        Oferta nuevaOferta = new Oferta(
                ofertaDto.getFecha(),
                publicacion.getId(),
                ofertaDto.getMonto(),
                ofertaDto.getCartasOfrecidas(),
                ofertaDto.getOfertante(),
                EstadoOferta.valueOf("PENDIENTE")
        );

        ofertaRepository.save(nuevaOferta);
    }

    // Ofertas recibidas en una publicacion
    @Override
    public List<OfertaDto> buscarOfertasPorPublicacion(Publicacion publicacion, Long idUsuario) {
        if(!publicacion.getPublicador().getId().equals(idUsuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tiene permiso para ver esta publicación");
        }

        return ofertaRepository.findByPublicacion(publicacion.getId()).stream()
                .map(oferta -> new OfertaDto(
                        oferta.getFecha(),
                        oferta.getIdPublicacion(),
                        oferta.getMonto(),
                        oferta.getCartasOfrecidas(),
                        oferta.getOfertante(),
                        oferta.getEstado().toString()))
                .collect(Collectors.toList());
    }

    @Override
    public void guardarOferta(Oferta oferta) {
        ofertaRepository.actualizarOferta(oferta);
    }

    @Override
    public void rechazarOtrasOfertas(Long idOferta, Long idPublicacion) {
        ofertaRepository.rechazarOtrasOfertas(idOferta, idPublicacion);
    }

    // Ofertas hechas por el usuario logueado a publicaciones de otros usuarios
    @Override
    public List<OfertaDto> buscarOfertasRealizadas(Long idUsuario) {
        List<Oferta> ofertasRealizadas = ofertaRepository.findByOfertante(idUsuario);

        return ofertasRealizadas.stream()
                .map(oferta -> new OfertaDto(
                        oferta.getFecha(),
                        oferta.getIdPublicacion(),
                        oferta.getMonto(),
                        oferta.getCartasOfrecidas(),
                        oferta.getOfertante(),
                        oferta.getEstado().toString()))
                .collect(Collectors.toList());
    }

}
