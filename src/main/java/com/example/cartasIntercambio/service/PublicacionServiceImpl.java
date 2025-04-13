package com.example.cartasIntercambio.service;

import com.example.cartasIntercambio.dto.CartaDto;
import com.example.cartasIntercambio.dto.PublicacionDto;
import com.example.cartasIntercambio.model.Mercado.Publicacion;
import com.example.cartasIntercambio.model.Producto_Carta.Carta;
import com.example.cartasIntercambio.repository.PublicacionRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublicacionServiceImpl implements IPublicacionService {
    private final PublicacionRepositoryImpl publicacionRepository;

    @Autowired
    public PublicacionServiceImpl(PublicacionRepositoryImpl publicacionRepository) {
        this.publicacionRepository = publicacionRepository;
    }

    
    @Override
    public List<PublicacionDto> listarPublicaciones() {
        List<Publicacion> publicaciones = publicacionRepository.findAll();

        return publicaciones.stream().map(publicacion -> new PublicacionDto(
                publicacion.getId(),
                publicacion.getFecha(),
                publicacion.getDescripcion(),
                publicacion.getDemanda(),
                publicacion.getOfertas(),
                publicacion.getPublicador(),
                publicacion.getEstado()
        )).collect(Collectors.toList());
    }
    
    @Override
    public void guardarPublicacion(PublicacionDto nuevaPublicacionDto) {
        Publicacion nuevaPublicacion = new Publicacion(
            null, //se sube sin ningun ID
            nuevaPublicacionDto.getFecha(),
            nuevaPublicacionDto.getDescripcion(),
            nuevaPublicacionDto.getDemanda(),
            nuevaPublicacionDto.getOfertas(),
            nuevaPublicacionDto.getPublicador(),
            nuevaPublicacionDto.getEstado()
        );
        publicacionRepository.save(nuevaPublicacion);
    }

    @Override
    public List<PublicacionDto> buscarPublicacionPorNombre(String nombre) {
        List<Publicacion> lista = publicacionRepository.findByCardName(nombre);
        
        return lista.stream().map(
        publicacion -> new PublicacionDto(
            publicacion.getId(),
            publicacion.getFecha(),
            publicacion.getDescripcion(),
            publicacion.getDemanda(),
            publicacion.getOfertas(),
            publicacion.getPublicador(),
            publicacion.getEstado()
        )).collect(Collectors.toList());
    }

    @Override
    public List<PublicacionDto> buscarPublicacionPorJuego(String juego) {
        List<Publicacion> lista = publicacionRepository.findByGameName(juego);
        
        return lista.stream().map(
        publicacion -> new PublicacionDto(
            publicacion.getId(),
            publicacion.getFecha(),
            publicacion.getDescripcion(),
            publicacion.getDemanda(),
            publicacion.getOfertas(),
            publicacion.getPublicador(),
            publicacion.getEstado()
        )).collect(Collectors.toList());
    }

    @Override
    public List<PublicacionDto> buscarPublicacionPorEstadoDeCarta(String estado) {
        List<Publicacion> lista = publicacionRepository.findByCardState(estado);
        
        return lista.stream().map(
        publicacion -> new PublicacionDto(
            publicacion.getId(),
            publicacion.getFecha(),
            publicacion.getDescripcion(),
            publicacion.getDemanda(),
            publicacion.getOfertas(),
            publicacion.getPublicador(),
            publicacion.getEstado()
        )).collect(Collectors.toList());
    }

    @Override
    public List<PublicacionDto> buscarPublicacionPorPrecio(Float precio) {
        List<Publicacion> lista = publicacionRepository.findByCost(precio);
        
        return lista.stream().map(
        publicacion -> new PublicacionDto(
            publicacion.getId(),
            publicacion.getFecha(),
            publicacion.getDescripcion(),
            publicacion.getDemanda(),
            publicacion.getOfertas(),
            publicacion.getPublicador(),
            publicacion.getEstado()
        )).collect(Collectors.toList());
    }

}
