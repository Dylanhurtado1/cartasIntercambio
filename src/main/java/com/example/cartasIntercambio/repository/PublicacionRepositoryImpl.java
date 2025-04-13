package com.example.cartasIntercambio.repository;

import com.example.cartasIntercambio.model.Mercado.Publicacion;
import com.example.cartasIntercambio.model.Usuario.Usuario;
import com.example.cartasIntercambio.repository.irepository.IPublicacionRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PublicacionRepositoryImpl implements IPublicacionRepository {

  private List<Publicacion> publicaciones = new ArrayList<>();

  @Override
  public void save(Publicacion NuevaPublicacion) {
    NuevaPublicacion.setId((long) (publicaciones.size() + 1)); //al no tener ninguna DB, por ahora el ID será la posición del array
    publicaciones.add(NuevaPublicacion);
  }

  @Override
  public List<Publicacion> findAll() {
    return publicaciones;
  }

  @Override
  public List<Publicacion> findByPublicador(Usuario publicador) {
    return publicaciones.stream().filter(publicacion -> publicacion.getPublicador().equals(publicador)).toList();
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
