package tea4life.recommendation_service.consumer;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tea4life.recommendation_service.dto.event.OrderPlacedEvent;
import tea4life.recommendation_service.service.RecommendationStatsService;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecommendationConsumer {

    RecommendationStatsService recommendationStatsService;

    @KafkaListener(topics = "${spring.kafka.topic.order-placed}")
    public void consumeOrderPlaced(OrderPlacedEvent event) {
        log.info("Received order placed event: {}", event == null ? null : event.orderId());
        recommendationStatsService.handleOrderPlaced(event);
    }
}
