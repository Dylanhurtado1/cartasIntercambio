package com.example.cartasIntercambio.service;

import com.example.cartasIntercambio.dto.CartaDto;
import com.example.cartasIntercambio.dto.PublicacionDto;
import com.example.cartasIntercambio.exception.PublicacionNoEncontradaException;
import com.example.cartasIntercambio.model.Mercado.EstadoPublicacion;
import com.example.cartasIntercambio.model.Mercado.Publicacion;
import com.example.cartasIntercambio.model.Producto_Carta.Carta;
import com.example.cartasIntercambio.model.Producto_Carta.EstadoCarta;
import com.example.cartasIntercambio.repository.irepository.IPublicacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
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
                .orElseThrow(() -> new PublicacionNoEncontradaException("No existe la publicación con id: " + idPublicacion));
    }

    public List<PublicacionDto> buscarPublicacionesFiltradas(String nombre, String juego, String estado,
                                                             BigDecimal preciomin, BigDecimal preciomax) {
        return publicacionRepository.findAll().stream()
                .filter(p -> nombre == null || p.getCartaOfrecida().getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .filter(p -> juego == null || p.getCartaOfrecida().getJuego().toLowerCase().contains(juego.toLowerCase()))
                .filter(p -> estado == null || p.getCartaOfrecida().getEstado().toString().toLowerCase().contains(estado.toLowerCase()))
                .filter(p -> preciomin == null || p.getPrecio().compareTo(preciomin) >= 0)
                .filter(p -> preciomax == null || p.getPrecio().compareTo(preciomax) <= 0)
                .map(PublicacionDto::new)
                .toList();
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
