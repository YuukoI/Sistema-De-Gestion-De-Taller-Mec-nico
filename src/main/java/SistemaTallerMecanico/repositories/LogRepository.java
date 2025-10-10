package SistemaTallerMecanico.repositories;

import SistemaTallerMecanico.entities.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {

    Page<Log> findAll(Pageable pageable);

    Page<Log> findByUsernameContainingIgnoreCaseOrActionContainingIgnoreCase(String username, String action, Pageable pageable);

}
