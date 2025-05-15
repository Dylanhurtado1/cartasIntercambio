package com.example.cartasIntercambio.service;

import com.example.cartasIntercambio.dto.PublicacionDto;
import com.example.cartasIntercambio.model.Mercado.EstadoPublicacion;
import com.example.cartasIntercambio.model.Mercado.Publicacion;
import com.example.cartasIntercambio.repository.irepository.IPublicacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublicacionServiceImpl implements IPublicacionService {
    private final IPublicacionRepository publicacionRepository;

    @Autowired
    public PublicacionServiceImpl(IPublicacionRepository publicacionRepository) {
        this.publicacionRepository = publicacionRepository;
    }

    public Publicacion buscarPublicacionPorId(String idPublicacion) {

        return publicacionRepository.findById(idPublicacion)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la publicación con id: " + idPublicacion));
    }

    @Override
    public PublicacionDto buscarPublicacionDTOPorId(String idPublicacion) { // Este es el que llamamos desde el controller
        Publicacion publicacion = buscarPublicacionPorId(idPublicacion);

        return new PublicacionDto(publicacion);
    }

    @Override
    public void finalizarPublicacion(String idPublicacion) {
        Publicacion publicacion = publicacionRepository.findById(idPublicacion)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe la publicación con id: " + idPublicacion));
        publicacion.setEstado(EstadoPublicacion.FINALIZADA);
        publicacionRepository.save(publicacion);
    }

    // TODO: Validar que exista el user
    @Override
    public List<PublicacionDto> buscarPublicacionesPorUsuario(String idUsuario) {

        return publicacionRepository.findByPublicadorId(idUsuario).stream()
                .map(PublicacionDto::new)
                .collect(Collectors.toList());

    }
    
    @Override
    public void guardarPublicacion(PublicacionDto nuevaPublicacionDto) {
        Publicacion nuevaPublicacion = new Publicacion(
            null, //se sube sin ningun ID
            nuevaPublicacionDto.getFecha(),
            nuevaPublicacionDto.getDescripcion(),
            nuevaPublicacionDto.getCartaOfrecida(),
            nuevaPublicacionDto.getPrecio(),
            nuevaPublicacionDto.getCartasInteres(),
            nuevaPublicacionDto.getPublicador(),
            EstadoPublicacion.valueOf("ACTIVA")
        );

        publicacionRepository.save(nuevaPublicacion);
    }


   public List<PublicacionDto> listarPublicaciones() {

        return publicacionRepository.findAll().stream()
                .map(PublicacionDto::new)
                .collect(Collectors.toList());
    }
}
