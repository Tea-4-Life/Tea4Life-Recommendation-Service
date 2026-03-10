package tea4life.recommendation_service.dto.response;

import java.time.Instant;

public record RecommendedOptionValueResponse(
        Long optionValueId,
        Double score,
        Instant lastUpdated
) {
}
