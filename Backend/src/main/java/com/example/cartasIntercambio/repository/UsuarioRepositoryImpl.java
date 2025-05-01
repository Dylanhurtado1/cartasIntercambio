package com.example.cartasIntercambio.repository;

import com.example.cartasIntercambio.model.Usuario.Usuario;
import com.example.cartasIntercambio.repository.irepository.IUsuarioRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UsuarioRepositoryImpl implements IUsuarioRepository {

    private List<Usuario> usuarios = new ArrayList<>();

    @Override
    public void save(Usuario usuario) {
        usuario.setId((long) (usuarios.size() + 1));
        usuarios.add(usuario);
    }

    @Override
    public List<Usuario> findAll() {
        return usuarios;
    }

}
