package tea4life.recommendation_service.service;

import tea4life.recommendation_service.dto.event.OrderPlacedEvent;
import tea4life.recommendation_service.dto.event.ProductClickedEvent;
import tea4life.recommendation_service.dto.event.ProductViewedEvent;

public interface RecommendationStatsService {
    void handleProductViewed(ProductViewedEvent event);

    void handleProductClicked(ProductClickedEvent event);

    void handleOrderPlaced(OrderPlacedEvent event);

    void applyPopularityDecay();
}
