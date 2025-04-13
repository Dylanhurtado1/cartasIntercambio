package com.example.cartasIntercambio.service;

import com.example.cartasIntercambio.dto.OfertaDto;
import com.example.cartasIntercambio.dto.PublicacionDto;
import com.example.cartasIntercambio.model.Mercado.Oferta;
import com.example.cartasIntercambio.model.Mercado.Publicacion;
import com.example.cartasIntercambio.repository.OfertaRepositoryImpl;
import com.example.cartasIntercambio.repository.PublicacionRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublicacionServiceImpl implements IPublicacionService {
    private final PublicacionRepositoryImpl publicacionRepository;
    private final OfertaRepositoryImpl ofertaRepository;

    @Autowired
    public PublicacionServiceImpl(PublicacionRepositoryImpl publicacionRepository, OfertaRepositoryImpl ofertaRepository) {
        this.publicacionRepository = publicacionRepository;
        this.ofertaRepository = ofertaRepository;
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

    @Override
    public void crearOferta(Long idPublicacion, OfertaDto ofertaDto) { // TODO: Ver si conviene crear OfertaService
        Oferta nuevaOferta = new Oferta(
                ofertaDto.getFecha(),
                ofertaDto.getPublicacion(),
                ofertaDto.getMonto(),
                ofertaDto.getCartasOfrecidas(),
                ofertaDto.getOfertante(),
                ofertaDto.getEstado()
        );

        publicacionRepository.findById(idPublicacion).get().agregarOferta(nuevaOferta);
        ofertaRepository.save(nuevaOferta);
    }

}
