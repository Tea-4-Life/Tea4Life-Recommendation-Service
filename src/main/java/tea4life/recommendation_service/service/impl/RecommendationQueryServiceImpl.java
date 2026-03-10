package tea4life.recommendation_service.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import tea4life.recommendation_service.dto.response.PopularProductResponse;
import tea4life.recommendation_service.dto.response.RecommendedOptionValueResponse;
import tea4life.recommendation_service.dto.response.RelatedProductResponse;
import tea4life.recommendation_service.model.ProductAssociation;
import tea4life.recommendation_service.model.ProductPopularity;
import tea4life.recommendation_service.model.constant.RecommendationAssociationType;
import tea4life.recommendation_service.repository.ProductAssociationRepository;
import tea4life.recommendation_service.repository.ProductPopularityRepository;
import tea4life.recommendation_service.service.RecommendationQueryService;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecommendationQueryServiceImpl implements RecommendationQueryService {

    ProductPopularityRepository productPopularityRepository;
    ProductAssociationRepository productAssociationRepository;

    @Override
    public List<PopularProductResponse> getPopularProducts() {
        return productPopularityRepository.findTop10ByOrderByTotalScoreDescLastUpdatedDesc()
                .stream()
                .map(this::toPopularResponse)
                .toList();
    }

    @Override
    public PopularProductResponse getProductPopularity(Long productId) {
        return productPopularityRepository.findByProductId(productId)
                .map(this::toPopularResponse)
                .orElseGet(() -> new PopularProductResponse(
                        productId,
                        0L,
                        0L,
                        0L,
                        0.0,
                        null
                ));
    }

    @Override
    public List<PopularProductResponse> getProductPopularities(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return List.of();
        }

        return productPopularityRepository.findByProductIdIn(productIds)
                .stream()
                .map(this::toPopularResponse)
                .toList();
    }

    @Override
    public List<RelatedProductResponse> getRelatedProducts(Long productId) {
        return productAssociationRepository
                .findTop20ByProductIdAndAssociationTypeOrderByCorrelationScoreDescLastUpdatedDesc(
                        productId,
                        RecommendationAssociationType.PRODUCT
                )
                .stream()
                .map(this::toRelatedResponse)
                .toList();
    }

    @Override
    public List<RecommendedOptionValueResponse> getRecommendedOptionValues(Long productId) {
        return productAssociationRepository
                .findTop20ByProductIdAndAssociationTypeOrderByCorrelationScoreDescLastUpdatedDesc(
                        productId,
                        RecommendationAssociationType.OPTION_VALUE
                )
                .stream()
                .map(this::toOptionValueResponse)
                .toList();
    }

    private PopularProductResponse toPopularResponse(ProductPopularity popularity) {
        return new PopularProductResponse(
                popularity.getProductId(),
                popularity.getViewCount(),
                popularity.getClickCount(),
                popularity.getOrderCount(),
                popularity.getTotalScore(),
                popularity.getLastUpdated()
        );
    }

    private RelatedProductResponse toRelatedResponse(ProductAssociation association) {
        return new RelatedProductResponse(
                association.getAssociatedTargetId(),
                association.getCorrelationScore(),
                association.getLastUpdated()
        );
    }

    private RecommendedOptionValueResponse toOptionValueResponse(ProductAssociation association) {
        return new RecommendedOptionValueResponse(
                association.getAssociatedTargetId(),
                association.getCorrelationScore(),
                association.getLastUpdated()
        );
    }
}
