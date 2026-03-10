package tea4life.recommendation_service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tea4life.recommendation_service.model.TrendingStat;

import java.time.LocalDate;
import java.util.Optional;

public interface TrendingStatRepository extends MongoRepository<TrendingStat, String> {
    Optional<TrendingStat> findByProductIdAndStatDate(Long productId, LocalDate statDate);
}
