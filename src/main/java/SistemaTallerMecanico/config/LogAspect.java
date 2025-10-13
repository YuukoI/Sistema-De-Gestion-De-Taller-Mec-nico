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
import java.util.Base64;

@Aspect
@Component
@AllArgsConstructor
public class LogAspect {

    private final LogRepository logRepository;
    private final HttpServletRequest request;
    private final ObjectMapper mapper = new ObjectMapper();

    @AfterReturning("execution(* SistemaTallerMecanico.controllers.*.*(..))")
    public void logControllerMethods(JoinPoint joinPoint) {
        String httpMethod = request.getMethod();

        if (!httpMethod.equalsIgnoreCase("POST") &&
                !httpMethod.equalsIgnoreCase("PUT") &&
                !httpMethod.equalsIgnoreCase("DELETE")) {
            return;
        }

        String methodName = joinPoint.getSignature().toShortString();
        String entity = joinPoint.getSignature().getDeclaringType().getSimpleName()
                .replace("Controller", "");

        String actionType = switch (httpMethod.toUpperCase()) {
            case "POST" -> "CREATE";
            case "PUT" -> "UPDATE";
            case "DELETE" -> "DELETE";
            default -> httpMethod.toUpperCase();
        };
        String action = actionType + " " + entity;

        String username;
        if (entity.equals("Auth") && methodName.contains("login")) {
            username = extractUsernameFromArgs(joinPoint);
        } else {
            username = extractUsernameFromTokenOrContext();
        }

        saveLog(username, action, joinPoint);
    }

    private String extractUsernameFromArgs(JoinPoint joinPoint) {
        for (Object arg : joinPoint.getArgs()) {
            try {
                Field f = arg.getClass().getDeclaredField("username");
                f.setAccessible(true);
                Object value = f.get(arg);
                if (value != null) return value.toString();
            } catch (NoSuchFieldException ignored) {
            } catch (Exception e) {
                System.out.println("⚠️ No se pudo leer el username del argumento: " + e.getMessage());
            }
        }
        return "ANONYMOUS";
    }

    private String extractUsernameFromTokenOrContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return auth.getName();
        }

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                String[] parts = token.split("\\.");
                if (parts.length == 3) {
                    String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
                    var node = mapper.readTree(payloadJson);

                    if (node.has("sub")) {
                        return node.get("sub").asText();
                    } else if (node.has("username")) {
                        return node.get("username").asText();
                    }
                }
            } catch (Exception e) {
                System.out.println("⚠️ No se pudo extraer el username del token: " + e.getMessage());
            }
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
