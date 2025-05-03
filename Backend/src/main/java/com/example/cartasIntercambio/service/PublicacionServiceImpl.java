package com.example.cartasIntercambio.service;

import com.example.cartasIntercambio.dto.OfertaDto;
import com.example.cartasIntercambio.dto.PublicacionDto;
import com.example.cartasIntercambio.model.Mercado.EstadoPublicacion;
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

//    public List<PublicacionDto> buscarPublicacionPorNombre(String nombre) {
//        List<Publicacion> lista = publicacionRepository.findByCardName(nombre);
//
//        return lista.stream().map(
//        publicacion -> new PublicacionDto(
//            publicacion.getId(),
//            publicacion.getFecha(),
//            publicacion.getDescripcion(),
//            publicacion.getDemanda(),
//            publicacion.getOfertas(),
//            publicacion.getPublicador(),
//            publicacion.getEstado()
//        )).collect(Collectors.toList());
//    }
//
//    @Override
//    public List<PublicacionDto> buscarPublicacionPorJuego(String juego) {
//        List<Publicacion> lista = publicacionRepository.findByGameName(juego);
//
//        return lista.stream().map(
//        publicacion -> new PublicacionDto(
//            publicacion.getId(),
//            publicacion.getFecha(),
//            publicacion.getDescripcion(),
//            publicacion.getDemanda(),
//            publicacion.getOfertas(),
//            publicacion.getPublicador(),
//            publicacion.getEstado()
//        )).collect(Collectors.toList());
//    }
//
//    @Override
//    public List<PublicacionDto> buscarPublicacionPorEstadoDeCarta(String estado) {
//        List<Publicacion> lista = publicacionRepository.findByCardState(estado);
//
//        return lista.stream().map(
//        publicacion -> new PublicacionDto(
//            publicacion.getId(),
//            publicacion.getFecha(),
//            publicacion.getDescripcion(),
//            publicacion.getDemanda(),
//            publicacion.getOfertas(),
//            publicacion.getPublicador(),
//            publicacion.getEstado()
//        )).collect(Collectors.toList());
//    }
//
//    @Override
//    public List<PublicacionDto> buscarPublicacionPorPrecio(BigDecimal precio) {
//        List<Publicacion> lista = publicacionRepository.findByCost(precio);
//
//        return lista.stream().map(
//        publicacion -> new PublicacionDto(
//            publicacion.getId(),
//            publicacion.getFecha(),
//            publicacion.getDescripcion(),
//            publicacion.getDemanda(),
//            publicacion.getOfertas(),
//            publicacion.getPublicador(),
//            publicacion.getEstado()
//        )).collect(Collectors.toList());
//    }

//    public void actualizarOferta(OfertaDto ofertaDto) {
//        Oferta ofertaActualizada = new Oferta(
//                ofertaDto.getId(),
//                ofertaDto.getFecha(),
//                ofertaDto.getIdPublicacion(),
//                //ofertaDto.getPublicacion(),
//                ofertaDto.getMonto(),
//                ofertaDto.getCartasOfrecidas(),
//                ofertaDto.getOfertante(),
//                "ACEPTADA"
//        );
//
//        //TODO: Save referancias a la publicacion en crear y en actualizar
//        ofertaRepository.save(ofertaActualizada); //TODO: Save deberia actualizar tambien
//    }
//    //TODO: DTOs?

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
