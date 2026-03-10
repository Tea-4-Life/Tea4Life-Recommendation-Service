package tea4life.recommendation_service.dto.response;

import java.time.Instant;

public record PopularProductResponse(
        Long productId,
        Long viewCount,
        Long clickCount,
        Long orderCount,
        Double totalScore,
        Instant lastUpdated
) {
}
