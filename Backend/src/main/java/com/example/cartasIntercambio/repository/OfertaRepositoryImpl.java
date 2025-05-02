package com.example.cartasIntercambio.repository;

import com.example.cartasIntercambio.model.Mercado.Oferta;
import com.example.cartasIntercambio.model.Usuario.Usuario;
import com.example.cartasIntercambio.repository.irepository.IOfertaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class OfertaRepositoryImpl implements IOfertaRepository {

  private List<Oferta> ofertas = new ArrayList<>();

  @Override
  public Optional<Oferta> findById(Long id) {
    if (id == null) {
      return Optional.empty();
    }

    return ofertas.stream().filter(
            oferta -> oferta.getId().equals(id)
    ).findFirst();
  }

  @Override
  public void save(Oferta oferta) {
    oferta.setId((long) (ofertas.size() + 1));
    ofertas.add(oferta);
  }

  @Override
  public List<Oferta> findAll() {
    return ofertas;
  }

  @Override
  public List<Oferta> findByOferente(Usuario oferente) {
    return ofertas.stream().filter(carta -> carta.getOfertante().equals(oferente)).toList();
  }

  @Override
  public List<Oferta> findByPublicacion(Long idPublicacion) {
    return ofertas.stream().filter(o -> o.getIdPublicacion().equals(idPublicacion)).toList();
  }
}
