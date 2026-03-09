package tea4life.recommendation_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tea4life.recommendation_service.model.ProductAssociation;
import tea4life.recommendation_service.model.constant.RecommendationAssociationType;

import java.util.Optional;

public interface ProductAssociationRepository extends JpaRepository<ProductAssociation, Long> {
    Optional<ProductAssociation> findByProductIdAndAssociatedTargetIdAndAssociationType(
            Long productId,
            Long associatedTargetId,
            RecommendationAssociationType associationType
    );
}
