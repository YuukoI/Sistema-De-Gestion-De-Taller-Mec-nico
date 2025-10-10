package SistemaTallerMecanico.services;

import SistemaTallerMecanico.entities.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LogService {

    Page<Log> findByUsernameContainingIgnoreCaseOrActionContainingIgnoreCase(String username, String action, Pageable pageable);

    Page<Log> findAll(Pageable pageable);

    void deleteAll();
}
