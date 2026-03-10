package tea4life.recommendation_service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tea4life.recommendation_service.model.UserPreference;

import java.util.Optional;

public interface UserPreferenceRepository extends MongoRepository<UserPreference, String> {
    Optional<UserPreference> findByUserIdAndCategoryId(Long userId, Long categoryId);
}
