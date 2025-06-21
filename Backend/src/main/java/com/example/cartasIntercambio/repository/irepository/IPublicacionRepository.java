package com.example.cartasIntercambio.repository.irepository;

import com.example.cartasIntercambio.model.Mercado.Publicacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;


public interface IPublicacionRepository extends MongoRepository<Publicacion, String> {



  Page<Publicacion> findByPublicadorId(String idUser, Pageable pageable);

  List<Publicacion> findByPublicadorId(String idUser);

  @Query("{ $and : [ " +
          "?#{ #nombre == null ? { $expr: true } : { 'cartaOfrecida.nombre' : #nombre } }, " +
          "?#{ #juego == null ? { $expr: true } : { 'cartaOfrecida.juego' : #juego } }, " +
          "?#{ #estado == null ? { $expr: true } : { 'cartaOfrecida.estado' : #estado } }, " +
          "?#{ #preciomin == null ? { $expr: true } : { precio : {$gte: #preciomin} }}, " +
          "?#{ #preciomax == null ? { $expr: true } : { precio : {$lte: #preciomax} } } " +
          "] }")
  Page<Publicacion> findByFiltros(String nombre, String juego, String estado,
                                  Double preciomin, Double preciomax, Pageable pageable);

}
