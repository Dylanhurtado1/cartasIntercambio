package com.example.cartasIntercambio.service;

import com.example.cartasIntercambio.dto.OfertaDto;
import com.example.cartasIntercambio.dto.PublicacionDto;
import com.example.cartasIntercambio.model.Mercado.Oferta;
import com.example.cartasIntercambio.model.Mercado.Publicacion;
import com.example.cartasIntercambio.repository.OfertaRepositoryImpl;
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
    private final OfertaRepositoryImpl ofertaRepository;

    @Autowired
    public PublicacionServiceImpl(PublicacionRepositoryImpl publicacionRepository, OfertaRepositoryImpl ofertaRepository) {
        this.publicacionRepository = publicacionRepository;
        this.ofertaRepository = ofertaRepository;
    }

    @Override
    public Publicacion buscarPublicacionPorId(Long idPublicacion) {

        return publicacionRepository.findById(idPublicacion)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la publicación"));
    }

    @Override
    public List<PublicacionDto> buscarPublicacionesPorUsuario(Long idUsuario) {
        List<Publicacion> publicaciones = publicacionRepository.findByPublicadorId(idUsuario); // TODO: Validar que exista el user

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
    public void crearOferta(Long idPublicacion, OfertaDto ofertaDto) {
        Oferta nuevaOferta = new Oferta(
                ofertaDto.getFecha(),
                ofertaDto.getPublicacion(),
                ofertaDto.getMonto(),
                ofertaDto.getCartasOfrecidas(),
                ofertaDto.getOfertante(),
                "PENDIENTE" // TODO: Luego cambiar por Enum
        );

        this.buscarPublicacionPorId(idPublicacion).agregarOferta(nuevaOferta);
        ofertaRepository.save(nuevaOferta);
    }

    public List<OfertaDto> buscarOfertasPorPublicacion(Long idPublicacion, Long idUsuario) {
        Publicacion publicacion = buscarPublicacionPorId(idPublicacion);

        if(!publicacion.getPublicador().getId().equals(idUsuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tiene permiso para ver esta publicación");
        }

        return publicacion.getOfertas().stream()
                .map(oferta -> new OfertaDto(
                        oferta.getFecha(),
                        oferta.getPublicacion(),
                        oferta.getMonto(),
                        oferta.getCartasOfrecidas(),
                        oferta.getOfertante(),
                        oferta.getEstado()))
                .collect(Collectors.toList());
    }

    // TODO: Filtrar ofertas hechas a publicaciones de otros usuarios (crear OfertaController y OfertaService)?

}
