package SistemaTallerMecanico.repositories;

import SistemaTallerMecanico.entities.Role;
import SistemaTallerMecanico.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByUsername(String username);

    Page<User> findAll(Pageable pageable);

    Page<User> findByUsernameContainingIgnoreCaseOrRole(String username, Role role, Pageable pageable);

    boolean existsByUsername(String username);
}
