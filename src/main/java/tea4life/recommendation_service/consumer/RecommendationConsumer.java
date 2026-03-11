package tea4life.recommendation_service.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tea4life.recommendation_service.dto.event.OrderPlacedEvent;
import tea4life.recommendation_service.dto.event.ProductClickedEvent;
import tea4life.recommendation_service.service.RecommendationStatsService;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecommendationConsumer {

    ObjectMapper objectMapper;
    RecommendationStatsService recommendationStatsService;

    @KafkaListener(topics = "${spring.kafka.topic.product-clicked}")
    public void consumeProductClicked(String payload) {
        ProductClickedEvent event = readPayload(payload, ProductClickedEvent.class);
        log.info("Received product clicked event: {}", event == null ? null : event.productId());
        recommendationStatsService.handleProductClicked(event);
    }

    @KafkaListener(topics = "${spring.kafka.topic.order-placed}")
    public void consumeOrderPlaced(String payload) {
        OrderPlacedEvent event = readPayload(payload, OrderPlacedEvent.class);
        log.info("Received order placed event: {}", event == null ? null : event.orderId());
        recommendationStatsService.handleOrderPlaced(event);
    }

    private <T> T readPayload(String payload, Class<T> targetType) {
        if (payload == null || payload.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(payload, targetType);
        } catch (JsonProcessingException ex) {
            log.warn("Khong the parse kafka payload sang {}: {}", targetType.getSimpleName(), ex.getMessage());
            return null;
        }
    }
}
