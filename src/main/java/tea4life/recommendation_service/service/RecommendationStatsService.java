package tea4life.recommendation_service.service;

import tea4life.recommendation_service.dto.event.OrderPlacedEvent;

public interface RecommendationStatsService {
    void handleOrderPlaced(OrderPlacedEvent event);
}
