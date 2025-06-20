package com.example.cartasIntercambio.repository.irepository;

import com.example.cartasIntercambio.model.Mercado.Publicacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;


public interface IPublicacionRepository extends MongoRepository<Publicacion, String> {



  List<Publicacion> findByPublicadorId(String idUser);

//  @Query("{ $and: [ " +
//          "{ $or: [ { $where: '?0 == null' }, { 'cartaOfrecida.nombre' : ?#{[0]} } ] }, " +
//          "{ $or: [ { $where: '?1 == null' }, { 'cartaOfrecida.juego' : ?#{[1]} } ] }, " +
//          "{ $or: [ { $where: '?2 == null' }, { 'cartaOfrecida.estado' : ?#{[2]} } ] }, " +
//          "{ $or: [ { $where: '?3 == null' }, { 'precio' : {$gt: ?#{[3]}} } ] }, " +
//          "{ $or: [ { $where: '?4 == null' }, { 'precio' : {$lt: ?#{[4]}} } ] } " +
//          "]}" )
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
