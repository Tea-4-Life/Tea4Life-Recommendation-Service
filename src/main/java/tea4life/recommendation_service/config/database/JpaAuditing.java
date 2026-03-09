package tea4life.recommendation_service.config.database;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import tea4life.recommendation_service.context.UserContext;

@Configuration
public class JpaAuditing {

    @Bean
    public AuditorAware<String> auditorAware() {
        return this::resolveCurrentEmail;
    }

    private Optional<String> resolveCurrentEmail() {
        Optional<String> fromContext = Optional.ofNullable(UserContext.get())
                .map(UserContext::getEmail)
                .filter(this::hasText);

        if (fromContext.isPresent()) {
            return fromContext;
        }

        return getAuthenticationEmail();
    }

    private Optional<String> getAuthenticationEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof String principalEmail && hasText(principalEmail)) {
            return Optional.of(principalEmail);
        }
        if (principal instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("email");
            if (!hasText(email)) {
                email = jwt.getClaimAsString("preferred_username");
            }
            if (hasText(email)) {
                return Optional.of(email);
            }
        }

        String name = authentication.getName();
        return hasText(name) ? Optional.of(name) : Optional.empty();
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

}
