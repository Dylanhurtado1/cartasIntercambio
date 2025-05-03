package com.example.cartasIntercambio.service;

import com.example.cartasIntercambio.dto.PublicacionDto;
import com.example.cartasIntercambio.model.Mercado.EstadoPublicacion;
import com.example.cartasIntercambio.model.Mercado.Publicacion;
import com.example.cartasIntercambio.repository.PublicacionRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublicacionServiceImpl implements IPublicacionService {
    private final PublicacionRepositoryImpl publicacionRepository;

    @Autowired
    public PublicacionServiceImpl(PublicacionRepositoryImpl publicacionRepository) {
        this.publicacionRepository = publicacionRepository;
    }

    public Publicacion buscarPublicacionPorId(Long idPublicacion) {

        return publicacionRepository.findById(idPublicacion)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la publicación con id: " + idPublicacion));
    }

    @Override
    public PublicacionDto buscarPublicacionDTOPorId(Long idPublicacion) { // Este es el que llamamos desde el controller
        Publicacion publicacion = buscarPublicacionPorId(idPublicacion);

        return new PublicacionDto(
                publicacion.getId(),
                publicacion.getFecha(),
                publicacion.getDescripcion(),
                publicacion.getCartaOfrecida(),
                publicacion.getPrecio(),
                publicacion.getCartasInteres(),
                publicacion.getPublicador(),
                publicacion.getEstado().toString());
    }

    @Override
    public void finalizarPublicacion(Long idPublicacion) {
        publicacionRepository.finalizarPublicacion(idPublicacion);
    }

    @Override
    public List<PublicacionDto> buscarPublicacionesPorUsuario(Long idUsuario) {
        List<Publicacion> publicaciones = publicacionRepository.findByPublicadorId(idUsuario); // TODO: Validar que exista el user

        return publicacionesADto(publicaciones);
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
            EstadoPublicacion.valueOf("ACTIVA") //nuevaPublicacionDto.getEstado()
        );

        publicacionRepository.save(nuevaPublicacion);
    }


   public List<PublicacionDto> listarPublicaciones() {
        List<Publicacion> publicaciones = publicacionRepository.findAll();

        return publicacionesADto(publicaciones);
    }

    public List<PublicacionDto> publicacionesADto(List<Publicacion> publicaciones) {

        return publicaciones.stream().map(publicacion -> new PublicacionDto(
                publicacion.getId(),
                publicacion.getFecha(),
                publicacion.getDescripcion(),
                publicacion.getCartaOfrecida(),
                publicacion.getPrecio(),
                publicacion.getCartasInteres(),
                publicacion.getPublicador(),
                publicacion.getEstado().toString()
        )).collect(Collectors.toList());
    }

}
