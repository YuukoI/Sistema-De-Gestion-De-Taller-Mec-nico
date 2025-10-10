package SistemaTallerMecanico.config;

import SistemaTallerMecanico.entities.Log;
import SistemaTallerMecanico.repositories.LogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
@AllArgsConstructor
public class LogAspect {

    private final LogRepository logRepository;
    private final HttpServletRequest request;
    private final ObjectMapper mapper = new ObjectMapper();

    @AfterReturning("execution(* SistemaTallerMecanico.controllers.*.*(..))")
    public void logControllerMethods(JoinPoint joinPoint) {
        String username = extractUsername();
        String httpMethod = request.getMethod();

        if (!httpMethod.equalsIgnoreCase("POST") &&
                !httpMethod.equalsIgnoreCase("PUT") &&
                !httpMethod.equalsIgnoreCase("DELETE")) {
            return;
        }

        String actionType = switch (httpMethod.toUpperCase()) {
            case "POST" -> "CREATE";
            case "PUT" -> "UPDATE";
            case "DELETE" -> "DELETE";
            default -> httpMethod.toUpperCase();
        };

        String entity = joinPoint.getSignature().getDeclaringType().getSimpleName()
                .replace("Controller", "");

        String action = actionType + " " + entity;

        saveLog(username, action, joinPoint);
    }

    private String extractUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return auth.getName();
        }
        return "ANONYMOUS";
    }

    private void saveLog(String username, String action, JoinPoint joinPoint) {
        Log log = new Log();
        log.setUsername(username);
        log.setAction(action);
        log.setMethod(joinPoint.getSignature().toShortString());
        log.setFecha(LocalDateTime.now());

        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            try {
                Object[] safeArgs = Arrays.stream(args)
                        .map(this::maskPassword)
                        .toArray();
                String details = mapper.writeValueAsString(safeArgs);

                if (details.length() > 255) {
                    details = details.substring(0, 255);
                }

                log.setDetails(details);
            } catch (Exception e) {
                String fallback = Arrays.toString(args);
                log.setDetails(fallback.length() > 255 ? fallback.substring(0, 255) : fallback);
            }
        }

        logRepository.save(log);
        System.out.println("LOG: " + username + " - " + action + " - " + log.getDetails());
    }

    private Object maskPassword(Object obj) {
        if (obj == null) return null;

        try {
            Field passwordField;
            try {
                passwordField = obj.getClass().getDeclaredField("password");
                passwordField.setAccessible(true);
                passwordField.set(obj, "******");
            } catch (NoSuchFieldException ignored) {
            }
        } catch (Exception ignored) {}

        return obj;
    }
}
