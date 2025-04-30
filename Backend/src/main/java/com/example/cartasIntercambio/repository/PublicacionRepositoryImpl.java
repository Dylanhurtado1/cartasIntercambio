package com.example.cartasIntercambio.repository;

import com.example.cartasIntercambio.model.Mercado.Publicacion;
import com.example.cartasIntercambio.model.Usuario.Usuario;
import com.example.cartasIntercambio.repository.irepository.IPublicacionRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class PublicacionRepositoryImpl implements IPublicacionRepository {

  private List<Publicacion> publicaciones = new ArrayList<>();

  @Override
  public void save(Publicacion NuevaPublicacion) {
    NuevaPublicacion.setId((long) (publicaciones.size() + 1)); //al no tener ninguna DB, por ahora el ID será la posición del
    publicaciones.add(NuevaPublicacion);
  }

  @Override
  public List<Publicacion> findAll() {
    return publicaciones;
  }

  @Override
  public List<Publicacion> findByPublicadorId(Long idUser) {
    return publicaciones.stream()
            .filter(publicacion -> publicacion.getPublicador().getId().equals(idUser))
            .collect(Collectors.toList());
  }

  @Override
  public Optional<Publicacion> findById(Long id) {
    if (id == null) {
      return Optional.empty();
    }

    return publicaciones.stream().filter(
            publicacion -> publicacion.getId().equals(id)
    ).findFirst();
  }

  public List<Publicacion> findByCardName(String nombreDeCartaBuscado) {
    return publicaciones.stream()
    .filter(
      publicacion -> publicacion.getDemanda().getCartaOfrecida().getNombre().contains(nombreDeCartaBuscado)
    ).collect(Collectors.toList());
  }

  @Override
  public List<Publicacion> findByGameName(String game) {
    return publicaciones.stream()
    .filter(
      publicacion -> publicacion.getDemanda().getCartaOfrecida().getJuego().contains(game)
    ).collect(Collectors.toList());
  }

  @Override
  public List<Publicacion> findByCardState(String state) {
    return publicaciones.stream()
    .filter(
      publicacion -> publicacion.getDemanda().getCartaOfrecida().getEstado().equals(state)
    ).collect(Collectors.toList());
  }

  @Override
  public List<Publicacion> findByCost(BigDecimal precio) {
    return publicaciones.stream()
    .filter(
      publicacion -> publicacion.getDemanda().getPrecio().equals(precio)
    ).collect(Collectors.toList());

  }

}
