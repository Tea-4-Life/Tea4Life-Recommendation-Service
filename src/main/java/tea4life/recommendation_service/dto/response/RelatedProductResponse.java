package tea4life.recommendation_service.dto.response;

import java.time.Instant;

public record RelatedProductResponse(
        Long productId,
        Double score,
        Instant lastUpdated
) {
}
