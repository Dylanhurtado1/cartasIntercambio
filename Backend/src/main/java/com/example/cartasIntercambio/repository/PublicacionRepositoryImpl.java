package com.example.cartasIntercambio.repository;

import com.example.cartasIntercambio.model.Mercado.EstadoPublicacion;
import com.example.cartasIntercambio.model.Mercado.Publicacion;
import com.example.cartasIntercambio.repository.irepository.IPublicacionRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

//@Repository
//public class PublicacionRepositoryImpl implements IPublicacionRepository {

  /*private List<Publicacion> publicaciones = new ArrayList<>();

  //@Override
/*  public void save(Publicacion nuevaPublicacion) {
    nuevaPublicacion.setId((long) (publicaciones.size() + 1)); //TODO: al no tener ninguna DB, por ahora el ID será la posición del
    publicaciones.add(nuevaPublicacion);
  }*/
/*
  @Override
  public <S extends Publicacion> S save(S entity) {
    return null;
  }

  @Override
  public <S extends Publicacion> List<S> saveAll(Iterable<S> entities) {
    return List.of();
  }

  @Override
  public List<Publicacion> findAll() {
    return publicaciones;
  }

  @Override
  public List<Publicacion> findAllById(Iterable<Long> longs) {
    return List.of();
  }

  @Override
  public long count() {
    return 0;
  }

  @Override
  public void deleteById(Long aLong) {

  }

  @Override
  public void delete(Publicacion entity) {

  }

  @Override
  public void deleteAllById(Iterable<? extends Long> longs) {

  }

  @Override
  public void deleteAll(Iterable<? extends Publicacion> entities) {

  }

  @Override
  public void deleteAll() {

  }

  @Override
  public List<Publicacion> findByPublicadorId(Long idUser) {
    return publicaciones.stream()
            .filter(publicacion -> publicacion.getPublicador().getId().equals(idUser))
            .collect(Collectors.toList());
  }

//  @Override
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

  @Override
  public boolean existsById(Long aLong) {
    return false;
  }

  @Override
  public <S extends Publicacion> S insert(S entity) {
    return null;
  }

  @Override
  public <S extends Publicacion> List<S> insert(Iterable<S> entities) {
    return List.of();
  }

  @Override
  public <S extends Publicacion> Optional<S> findOne(Example<S> example) {
    return Optional.empty();
  }

  @Override
  public <S extends Publicacion> List<S> findAll(Example<S> example) {
    return List.of();
  }

  @Override
  public <S extends Publicacion> List<S> findAll(Example<S> example, Sort sort) {
    return List.of();
  }

  @Override
  public <S extends Publicacion> Page<S> findAll(Example<S> example, Pageable pageable) {
    return null;
  }

  @Override
  public <S extends Publicacion> long count(Example<S> example) {
    return 0;
  }

  @Override
  public <S extends Publicacion> boolean exists(Example<S> example) {
    return false;
  }

  @Override
  public <S extends Publicacion, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
    return null;
  }

  @Override
  public List<Publicacion> findAll(Sort sort) {
    return List.of();
  }

  @Override
  public Page<Publicacion> findAll(Pageable pageable) {
    return null;
  }

}*/
