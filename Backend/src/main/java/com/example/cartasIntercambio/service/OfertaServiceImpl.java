package com.example.cartasIntercambio.service;

import com.example.cartasIntercambio.dto.OfertaDto;
import com.example.cartasIntercambio.exception.PublicacionNoEncontradaException;
import com.example.cartasIntercambio.model.Mercado.EstadoOferta;
import com.example.cartasIntercambio.model.Mercado.EstadoPublicacion;
import com.example.cartasIntercambio.model.Mercado.Oferta;
import com.example.cartasIntercambio.model.Mercado.Publicacion;
import com.example.cartasIntercambio.repository.irepository.IOfertaRepository;
import com.example.cartasIntercambio.repository.irepository.IPublicacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OfertaServiceImpl implements IOfertaService{
    private final IOfertaRepository ofertaRepository;
    private final IPublicacionRepository publicacionRepository;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public OfertaServiceImpl(IOfertaRepository ofertaRepository, IPublicacionRepository publicacionRepository, MongoTemplate mongoTemplate) {
        this.ofertaRepository = ofertaRepository;
        this.publicacionRepository = publicacionRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public OfertaDto buscarOfertaDto(String idOferta) {
        Oferta oferta = buscarOfertaPorId(idOferta);

        return new OfertaDto(oferta);
    }

    public Oferta buscarOfertaPorId(String idOferta) {

        return ofertaRepository.findById(idOferta)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la oferta con id: " + idOferta));
    }

    @Override
    public OfertaDto crearOferta(OfertaDto ofertaDto, Publicacion publicacion) {
        if(!publicacion.getEstado().equals(EstadoPublicacion.ACTIVA)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La publicación ya no está activa.");
        }
        Oferta nuevaOferta = new Oferta(
                ofertaDto.getFecha(),
                publicacion.getId(),
                ofertaDto.getMonto(),
                ofertaDto.getCartasOfrecidas(),
                ofertaDto.getOfertante(),
                EstadoOferta.valueOf("PENDIENTE")
        );

        Oferta guardada = ofertaRepository.save(nuevaOferta);
        return new OfertaDto(guardada);
    }

    // Ofertas recibidas en una publicacion
    @Override
    public List<OfertaDto> buscarOfertasPorPublicacion(Publicacion publicacion, String idUsuario) {
        if(!publicacion.getPublicador().getId().equals(idUsuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tiene permiso para ver esta publicación");
        }

        return ofertaRepository.findByIdPublicacion(publicacion.getId()).stream()
                .map(OfertaDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<OfertaDto> buscarOfertasPorPublicacion(String idPublicacion) {
        if (!publicacionRepository.existsById(idPublicacion)) {
            throw new PublicacionNoEncontradaException("No existe la publicación con id: " + idPublicacion);
        }
        return ofertaRepository.findByIdPublicacion(idPublicacion).stream()
                .map(OfertaDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public void guardarOferta(Oferta oferta) {
        Query query = new Query(
                Criteria.where("_id").is(oferta.getId())
                        .and("version").is(oferta.getVersion())
        );
        Update update = new Update()
                .set("estado", oferta.getEstado())
                .set("monto", oferta.getMonto())
                .set("cartasOfrecidas", oferta.getCartasOfrecidas())
                .set("ofertante", oferta.getOfertante())
                .set("fecha", oferta.getFecha())
                .set("idPublicacion", oferta.getIdPublicacion());

        Oferta actualizada = mongoTemplate.findAndModify(query, update, Oferta.class);

        if (actualizada == null) {
            throw new RuntimeException("La oferta fue modificada por otro usuario (o no existe).");
        }
    }

    @Override
    public void rechazarOtrasOfertas(String idOferta, String idPublicacion) {
        Query query = new Query(
                Criteria.where("idPublicacion").is(idPublicacion)
                        .and("_id").ne(idOferta)
        );
        Update update = new Update().set("estado", EstadoOferta.RECHAZADO);
        mongoTemplate.updateMulti(query, update, Oferta.class);
    }

    // Ofertas hechas por el usuario logueado a publicaciones de otros usuarios
    @Override
    public List<OfertaDto> buscarOfertasRealizadas(String idUsuario) {
        List<Oferta> ofertasRealizadas = ofertaRepository.findByOfertante_Id(idUsuario);

        return ofertasRealizadas.stream()
                .map(OfertaDto::new)
                .collect(Collectors.toList());
    }

}
