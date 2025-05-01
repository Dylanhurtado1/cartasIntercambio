package com.example.cartasIntercambio.repository;

import com.example.cartasIntercambio.model.Usuario.Usuario;
import com.example.cartasIntercambio.repository.irepository.IUsuarioRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Override
    public Optional<Usuario> findById(Long id) {
        return usuarios.stream().filter(u -> u.getId().equals(id)).findFirst();
    }

    @Override
    public boolean existsByUser(String user) {
        return usuarios.stream().anyMatch(u -> u.getUser().equalsIgnoreCase(user));
    }

    @Override
    public boolean existsByCorreo(String correo) {
        return usuarios.stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(correo));
    }


}
