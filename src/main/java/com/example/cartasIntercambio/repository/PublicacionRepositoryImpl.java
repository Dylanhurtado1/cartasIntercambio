package com.example.cartasIntercambio.repository;

import com.example.cartasIntercambio.model.Mercado.Publicacion;
import com.example.cartasIntercambio.model.Usuario.Usuario;
import com.example.cartasIntercambio.repository.irepository.IPublicacionRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
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
  public List<Publicacion> findByPublicador(Usuario publicador) {
    return publicaciones.stream().filter(publicacion -> publicacion.getPublicador().equals(publicador)).toList();
  }

  @Override
  public List<Publicacion> findByCardName(String nombreDeCartaBuscado){
    return publicaciones.stream()
    .filter(
      publicacion -> publicacion.getDemanda().getCartasOfrecidas().stream()
        .anyMatch(carta ->
          carta.getNombre().equals(nombreDeCartaBuscado)
        )
    ).collect(Collectors.toList());
  }

  @Override
  public List<Publicacion> findByGameName(String game) {
    return publicaciones.stream()
    .filter(
      publicacion -> publicacion.getDemanda().getCartasOfrecidas().stream()
        .anyMatch(carta -> 
          carta.getJuego().equals(game)
        )
    ).collect(Collectors.toList());
  }

  @Override
  public List<Publicacion> findByCardState(String state){
    return publicaciones.stream()
    .filter(
      publicacion -> publicacion.getDemanda().getCartasOfrecidas().stream()
        .anyMatch(carta -> 
          carta.getEstado().equals(state)
        )
    ).collect(Collectors.toList());
  }

  @Override
  public List<Publicacion> findByCost(Float precio){
    return publicaciones.stream()
    .filter(
      publicacion -> publicacion.getDemanda().getPrecio().equals(precio)
    ).collect(Collectors.toList());
  }

}
