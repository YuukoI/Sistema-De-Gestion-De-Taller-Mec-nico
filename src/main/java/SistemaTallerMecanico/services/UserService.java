package SistemaTallerMecanico.services;

import SistemaTallerMecanico.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAll();

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    User save(User user);

    void deleteById(Long id);

    Page<User> findAllPaged(Pageable pageable);

    Page<User> findByUsernameContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String keyword, Pageable pageable);

}