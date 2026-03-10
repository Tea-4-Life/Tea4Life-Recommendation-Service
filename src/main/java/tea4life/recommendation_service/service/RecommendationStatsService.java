package tea4life.recommendation_service.service;

import tea4life.recommendation_service.dto.event.OrderPlacedEvent;
import tea4life.recommendation_service.dto.event.ProductClickedEvent;

public interface RecommendationStatsService {
    void handleProductClicked(ProductClickedEvent event);

    void handleOrderPlaced(OrderPlacedEvent event);

    void applyPopularityDecay();
}
