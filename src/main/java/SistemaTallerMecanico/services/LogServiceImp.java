package SistemaTallerMecanico.services;

import SistemaTallerMecanico.entities.Log;
import SistemaTallerMecanico.repositories.LogRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LogServiceImp implements LogService {

    private final LogRepository logRepository;

    @Override
    public Page<Log> findByUsernameContainingIgnoreCaseOrActionContainingIgnoreCase(String username, String action, Pageable pageable) {
        return logRepository.findByUsernameContainingIgnoreCaseOrActionContainingIgnoreCase(username, action, pageable);
    }

    @Override
    public Page<Log> findAll(Pageable pageable) {
        return logRepository.findAll(pageable);
    }

    @Override
    public void deleteAll() {
        logRepository.deleteAll();
    }
}
