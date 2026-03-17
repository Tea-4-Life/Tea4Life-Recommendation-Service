package tea4life.recommendation_service.service;

import tea4life.recommendation_service.dto.response.PopularProductResponse;
import tea4life.recommendation_service.dto.response.RecommendedOptionValueResponse;
import tea4life.recommendation_service.dto.response.RelatedProductResponse;

import java.util.List;

public interface RecommendationQueryService {
    List<PopularProductResponse> getPopularProducts(int limit);

    PopularProductResponse getProductPopularity(Long productId);

    List<PopularProductResponse> getProductPopularities(List<Long> productIds);

    List<RelatedProductResponse> getRelatedProducts(Long productId);

    List<RecommendedOptionValueResponse> getRecommendedOptionValues(Long productId);
}
