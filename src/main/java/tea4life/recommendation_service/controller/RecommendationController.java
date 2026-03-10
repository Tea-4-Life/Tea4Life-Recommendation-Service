package tea4life.recommendation_service.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tea4life.recommendation_service.dto.base.ApiResponse;
import tea4life.recommendation_service.dto.response.PopularProductResponse;
import tea4life.recommendation_service.dto.response.RecommendedOptionValueResponse;
import tea4life.recommendation_service.dto.response.RelatedProductResponse;
import tea4life.recommendation_service.service.RecommendationQueryService;

import java.util.List;

@RestController
@RequestMapping("/public/recommendations")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecommendationController {

    RecommendationQueryService recommendationQueryService;

    @GetMapping("/popular")
    public ApiResponse<List<PopularProductResponse>> getPopularProducts() {
        return new ApiResponse<>(recommendationQueryService.getPopularProducts());
    }

    @GetMapping("/products/{productId}/related")
    public ApiResponse<List<RelatedProductResponse>> getRelatedProducts(
            @PathVariable("productId") Long productId
    ) {
        return new ApiResponse<>(recommendationQueryService.getRelatedProducts(productId));
    }

    @GetMapping("/products/{productId}/option-values")
    public ApiResponse<List<RecommendedOptionValueResponse>> getRecommendedOptionValues(
            @PathVariable("productId") Long productId
    ) {
        return new ApiResponse<>(recommendationQueryService.getRecommendedOptionValues(productId));
    }
}
