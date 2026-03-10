package tea4life.recommendation_service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tea4life.recommendation_service.model.ProductAssociation;
import tea4life.recommendation_service.model.constant.RecommendationAssociationType;

import java.util.List;
import java.util.Optional;

public interface ProductAssociationRepository extends MongoRepository<ProductAssociation, String> {
    Optional<ProductAssociation> findByProductIdAndAssociatedTargetIdAndAssociationType(
            Long productId,
            Long associatedTargetId,
            RecommendationAssociationType associationType
    );

    List<ProductAssociation> findTop20ByProductIdAndAssociationTypeOrderByCorrelationScoreDescLastUpdatedDesc(
            Long productId,
            RecommendationAssociationType associationType
    );
}
