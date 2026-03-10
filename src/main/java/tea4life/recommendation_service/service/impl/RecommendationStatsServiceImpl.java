package tea4life.recommendation_service.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import tea4life.recommendation_service.dto.event.OrderPlacedEvent;
import tea4life.recommendation_service.dto.event.OrderPlacedItemEvent;
import tea4life.recommendation_service.dto.event.ProductClickedEvent;
import tea4life.recommendation_service.dto.event.ProductViewedEvent;
import tea4life.recommendation_service.model.ProductAssociation;
import tea4life.recommendation_service.model.ProductPopularity;
import tea4life.recommendation_service.model.constant.RecommendationAssociationType;
import tea4life.recommendation_service.repository.ProductAssociationRepository;
import tea4life.recommendation_service.repository.ProductPopularityRepository;
import tea4life.recommendation_service.service.RecommendationStatsService;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecommendationStatsServiceImpl implements RecommendationStatsService {

    static final double VIEW_WEIGHT = 1.0;
    static final double CLICK_WEIGHT = 2.0;
    static final double ORDER_WEIGHT = 10.0;
    static final double PRODUCT_ASSOCIATION_WEIGHT = 5.0;
    static final double OPTION_VALUE_ASSOCIATION_WEIGHT = 3.0;

    final ProductAssociationRepository productAssociationRepository;
    final ProductPopularityRepository productPopularityRepository;
    final MongoTemplate mongoTemplate;

    @Value("${recommendation.popularity.decay-factor:0.5}")
    double popularityDecayFactor;

    @Override
    public void handleProductViewed(ProductViewedEvent event) {
        if (event == null || event.productId() == null) {
            return;
        }
        upsertProductPopularity(event.productId(), 1L, 0L, 0L, VIEW_WEIGHT);
    }

    @Override
    public void handleProductClicked(ProductClickedEvent event) {
        if (event == null || event.productId() == null) {
            return;
        }
        upsertProductPopularity(event.productId(), 0L, 1L, 0L, CLICK_WEIGHT);
    }

    @Override
    public void handleOrderPlaced(OrderPlacedEvent event) {
        if (event == null || event.items() == null || event.items().isEmpty()) {
            return;
        }

        List<OrderPlacedItemEvent> items = event.items().stream()
                .filter(this::isValidItem)
                .toList();

        if (items.isEmpty()) {
            return;
        }

        for (OrderPlacedItemEvent item : items) {
            int quantity = quantityOrDefault(item);
            upsertProductPopularity(
                    item.productId(),
                    0L,
                    0L,
                    (long) quantity,
                    quantity * ORDER_WEIGHT
            );
            upsertOptionValueAssociations(item.productId(), item.optionValueIds(), quantity);
        }

        upsertProductAssociations(items);
    }

    @Override
    public void applyPopularityDecay() {
        if (popularityDecayFactor <= 0 || popularityDecayFactor >= 1) {
            return;
        }

        List<ProductPopularity> popularities = productPopularityRepository.findAll();
        Instant now = Instant.now();
        for (ProductPopularity popularity : popularities) {
            popularity.setTotalScore(popularity.getTotalScore() * popularityDecayFactor);
            popularity.setLastUpdated(now);
        }
        productPopularityRepository.saveAll(popularities);
    }

    private boolean isValidItem(OrderPlacedItemEvent item) {
        return item != null && item.productId() != null;
    }

    private void upsertProductPopularity(
            Long productId,
            long viewIncrement,
            long clickIncrement,
            long orderIncrement,
            double scoreIncrement
    ) {
        Query query = Query.query(Criteria.where("product_id").is(productId));
        Update update = new Update()
                .setOnInsert("product_id", productId)
                .setOnInsert("view_count", 0L)
                .setOnInsert("click_count", 0L)
                .setOnInsert("order_count", 0L)
                .setOnInsert("total_score", 0.0)
                .setOnInsert("active", true)
                .inc("view_count", viewIncrement)
                .inc("click_count", clickIncrement)
                .inc("order_count", orderIncrement)
                .inc("total_score", scoreIncrement)
                .set("last_updated", Instant.now());

        mongoTemplate.upsert(query, update, ProductPopularity.class);
    }

    private void upsertOptionValueAssociations(Long productId, List<Long> optionValueIds, int quantity) {
        for (Long optionValueId : safeList(optionValueIds)) {
            if (optionValueId == null) {
                continue;
            }
            upsertAssociation(
                    productId,
                    optionValueId,
                    RecommendationAssociationType.OPTION_VALUE,
                    quantity * OPTION_VALUE_ASSOCIATION_WEIGHT
            );
        }
    }

    private void upsertProductAssociations(List<OrderPlacedItemEvent> items) {
        for (int i = 0; i < items.size(); i++) {
            for (int j = 0; j < items.size(); j++) {
                if (i == j) {
                    continue;
                }
                Long sourceProductId = items.get(i).productId();
                Long associatedProductId = items.get(j).productId();
                if (sourceProductId == null || associatedProductId == null || sourceProductId.equals(associatedProductId)) {
                    continue;
                }

                double increment = Math.max(quantityOrDefault(items.get(i)), quantityOrDefault(items.get(j)))
                        * PRODUCT_ASSOCIATION_WEIGHT;
                upsertAssociation(
                        sourceProductId,
                        associatedProductId,
                        RecommendationAssociationType.PRODUCT,
                        increment
                );
            }
        }
    }

    private void upsertAssociation(
            Long productId,
            Long associatedTargetId,
            RecommendationAssociationType associationType,
            double increment
    ) {
        Query query = Query.query(new Criteria().andOperator(
                Criteria.where("product_id").is(productId),
                Criteria.where("associated_target_id").is(associatedTargetId),
                Criteria.where("association_type").is(associationType)
        ));

        Update update = new Update()
                .setOnInsert("product_id", productId)
                .setOnInsert("associated_target_id", associatedTargetId)
                .setOnInsert("association_type", associationType)
                .setOnInsert("correlation_score", 0.0)
                .setOnInsert("active", true)
                .inc("correlation_score", increment)
                .set("last_updated", Instant.now());

        mongoTemplate.upsert(query, update, ProductAssociation.class);
    }

    private int quantityOrDefault(OrderPlacedItemEvent item) {
        return item.quantity() == null || item.quantity() <= 0 ? 1 : item.quantity();
    }

    private List<Long> safeList(List<Long> values) {
        return values == null ? Collections.emptyList() : values;
    }
}
