package tea4life.recommendation_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tea4life.recommendation_service.model.TrendingStat;

import java.time.LocalDate;
import java.util.Optional;

public interface TrendingStatRepository extends JpaRepository<TrendingStat, Long> {
    Optional<TrendingStat> findByProductIdAndStatDate(Long productId, LocalDate statDate);
}
