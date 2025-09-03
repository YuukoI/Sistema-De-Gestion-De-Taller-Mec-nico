package SistemaTallerMecanico.services;

import SistemaTallerMecanico.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAll();

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    User save(User user);

    User editUser(User user);

    void deleteById(Long id);

}