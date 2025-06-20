package com.example.cartasIntercambio.service;

import com.example.cartasIntercambio.dto.CartaDto;
import com.example.cartasIntercambio.dto.PublicacionDto;
import com.example.cartasIntercambio.dto.PublicacionResponse;
import com.example.cartasIntercambio.exception.PublicacionNoEncontradaException;
import com.example.cartasIntercambio.model.Mercado.EstadoPublicacion;
import com.example.cartasIntercambio.model.Mercado.Publicacion;
import com.example.cartasIntercambio.model.Producto_Carta.Carta;
import com.example.cartasIntercambio.model.Producto_Carta.EstadoCarta;
import com.example.cartasIntercambio.repository.irepository.IPublicacionRepository;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;

@Service
public class PublicacionServiceImpl implements IPublicacionService {
    private final IPublicacionRepository publicacionRepository;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public PublicacionServiceImpl(IPublicacionRepository publicacionRepository, MongoTemplate mongoTemplate) {
        this.publicacionRepository = publicacionRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public Publicacion buscarPublicacionPorId(String idPublicacion) {
        return publicacionRepository.findById(idPublicacion)
                .orElseThrow(() -> new PublicacionNoEncontradaException("No existe la publicación con id: " + idPublicacion));
    }

    public PublicacionResponse buscarPublicacionesFiltradas(String nombre, String juego, String estado,
                                                            Double preciomin, Double preciomax, int pageNo, int pageSize) {

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Publicacion> paginaPublicaciones = publicacionRepository.findByFiltros(nombre, juego, estado, preciomin, preciomax, pageable);
        List<Publicacion> publicaciones = paginaPublicaciones.getContent();
        List<PublicacionDto> content = publicaciones.stream().map(PublicacionDto::new).toList();

        return new PublicacionResponse(content, paginaPublicaciones.getNumber(),
                paginaPublicaciones.getSize(), paginaPublicaciones.getTotalElements(),
                paginaPublicaciones.getTotalPages(), paginaPublicaciones.isLast());
    }

    @Override
    public PublicacionDto buscarPublicacionDTOPorId(String idPublicacion) {
        Publicacion publicacion = buscarPublicacionPorId(idPublicacion);
        return new PublicacionDto(publicacion);
    }

    @Override
    public void finalizarPublicacion(String idPublicacion) {
        Publicacion publicacion = publicacionRepository.findById(idPublicacion)
                .orElseThrow(() -> new PublicacionNoEncontradaException("No existe la publicación con id: " + idPublicacion));
        publicacion.setEstado(EstadoPublicacion.FINALIZADA);
        publicacionRepository.save(publicacion);
    }

    @Override
    public Document contarPublicacionesPorJuego() {

        GroupOperation groupOp = group("cartaOfrecida.juego").count().as("totalJuego");
        ProjectionOperation project = Aggregation.project("totalJuego");
        UnionWithOperation unionWithCount = UnionWithOperation.unionWith("Publicacion")
                .pipeline(Aggregation.count().as("Total"));

        Aggregation aggregation = Aggregation.newAggregation(groupOp, project, unionWithCount);

        return mongoTemplate.aggregate(aggregation, "Publicacion", Publicacion.class).getRawResults();
    }

    // TODO: Validar que exista el user
    @Override
    public List<PublicacionDto> buscarPublicacionesPorUsuario(String idUsuario) {

        return publicacionRepository.findByPublicadorId(idUsuario).stream()
                .map(PublicacionDto::new)
                .collect(Collectors.toList());

    }

    @Override
    public PublicacionDto guardarPublicacion(PublicacionDto nuevaPublicacionDto) {
        Publicacion nuevaPublicacion = new Publicacion(
                null,
                nuevaPublicacionDto.getFecha(),
                nuevaPublicacionDto.getDescripcion(),
                dtoToCarta(nuevaPublicacionDto.getCartaOfrecida()),        // <--- ¡nuevo!
                nuevaPublicacionDto.getPrecio(),
                dtosToCartas(nuevaPublicacionDto.getCartasInteres()),      // <--- ¡nuevo!
                nuevaPublicacionDto.getPublicador(),
                EstadoPublicacion.valueOf("ACTIVA")
        );
        Publicacion guardada = publicacionRepository.save(nuevaPublicacion);
        return new PublicacionDto(guardada);
    }


    private Carta dtoToCarta(CartaDto dto) {
        if (dto == null) return null;
        Carta carta = new Carta();
        carta.setJuego(dto.getJuego());
        carta.setNombre(dto.getNombre());
        // Si tu entidad usa Enum para estado, convertí:
        carta.setEstado(EstadoCarta.valueOf(dto.getEstado()));
        carta.setImagenes(dto.getImagenes()); // Lista de URLs S3
        return carta;
    }

    private List<Carta> dtosToCartas(List<CartaDto> dtos) {
        if (dtos == null) return null;
        List<Carta> out = new ArrayList<>();
        for (CartaDto dto : dtos) {
            out.add(dtoToCarta(dto));
        }
        return out;
    }


   public List<PublicacionDto> listarPublicaciones() {

        return publicacionRepository.findAll().stream()
                .map(PublicacionDto::new)
                .collect(Collectors.toList());
    }
}
