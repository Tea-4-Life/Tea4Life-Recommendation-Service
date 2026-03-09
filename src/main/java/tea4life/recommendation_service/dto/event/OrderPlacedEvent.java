package tea4life.recommendation_service.dto.event;

import java.util.List;

public record OrderPlacedEvent(
        String orderId,
        Long userId,
        List<OrderPlacedItemEvent> items
) {
}
