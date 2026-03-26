package tea4life.recommendation_service.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tea4life.recommendation_service.dto.base.ApiResponse;
import tea4life.recommendation_service.dto.response.PopularProductResponse;
import tea4life.recommendation_service.dto.response.RecommendedOptionValueResponse;
import tea4life.recommendation_service.dto.response.RelatedProductResponse;
import tea4life.recommendation_service.service.RecommendationQueryService;

import java.util.List;

@RestController
@RequestMapping("/internal/recommendations")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecommendationController {

    RecommendationQueryService recommendationQueryService;

    @GetMapping("/products/popularity/top")
    public ApiResponse<List<PopularProductResponse>> getPopularProducts(
            @RequestParam(name = "limit", defaultValue = "10") int limit
    ) {
        return new ApiResponse<>(recommendationQueryService.getPopularProducts(limit));
    }

    @GetMapping("/products/{productId}/popularity")
    public ApiResponse<PopularProductResponse> getProductPopularityById(
            @PathVariable("productId") Long productId
    ) {
        return new ApiResponse<>(recommendationQueryService.getProductPopularityById(productId));
    }

    @GetMapping("/products/popularity")
    public ApiResponse<List<PopularProductResponse>> getProductPopularises(
            @RequestParam("productIds") List<Long> productIds
    ) {
        return new ApiResponse<>(recommendationQueryService.getProductPopularities(productIds));
    }
}
