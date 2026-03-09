package tea4life.recommendation_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tea4life.recommendation_service.model.UserPreference;

import java.util.Optional;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {
    Optional<UserPreference> findByUserIdAndCategoryId(Long userId, Long categoryId);
}
