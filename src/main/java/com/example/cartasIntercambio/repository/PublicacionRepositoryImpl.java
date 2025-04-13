package com.example.cartasIntercambio.repository;

import com.example.cartasIntercambio.model.Mercado.Publicacion;
import com.example.cartasIntercambio.model.Usuario.Usuario;
import com.example.cartasIntercambio.repository.irepository.IPublicacionRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PublicacionRepositoryImpl implements IPublicacionRepository {

  private List<Publicacion> publicaciones = new ArrayList<>();

  @Override
  public void save(Publicacion NuevaPublicacion) {
    NuevaPublicacion.setId((long) (publicaciones.size() + 1));
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
}
