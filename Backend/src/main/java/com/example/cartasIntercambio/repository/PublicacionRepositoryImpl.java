package com.example.cartasIntercambio.repository;

import com.example.cartasIntercambio.model.Mercado.EstadoPublicacion;
import com.example.cartasIntercambio.model.Mercado.Publicacion;
import com.example.cartasIntercambio.repository.irepository.IPublicacionRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public abstract class PublicacionRepositoryImpl implements IPublicacionRepository {

  private List<Publicacion> publicaciones = new ArrayList<>();

  @Override
  public Publicacion save(Publicacion NuevaPublicacion) {
    NuevaPublicacion.setId((long) (publicaciones.size() + 1)); //TODO: al no tener ninguna DB, por ahora el ID será la posición del
    publicaciones.add(NuevaPublicacion);
    return null;
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
  public void finalizarPublicacion(Long idPublicacion) {
    publicaciones.get(idPublicacion.intValue() - 1).setEstado(EstadoPublicacion.FINALIZADA);
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

}
