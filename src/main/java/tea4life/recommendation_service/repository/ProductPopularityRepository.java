package tea4life.recommendation_service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tea4life.recommendation_service.model.ProductPopularity;

import java.util.List;
import java.util.Optional;

public interface ProductPopularityRepository extends MongoRepository<ProductPopularity, String> {
    Optional<ProductPopularity> findByProductId(Long productId);

    List<ProductPopularity> findTop20ByOrderByTotalScoreDescLastUpdatedDesc();

    List<ProductPopularity> findTop10ByOrderByTotalScoreDescLastUpdatedDesc();
}
