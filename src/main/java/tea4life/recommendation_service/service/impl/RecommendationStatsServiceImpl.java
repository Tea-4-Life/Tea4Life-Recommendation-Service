package tea4life.recommendation_service.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import tea4life.recommendation_service.dto.event.OrderPlacedEvent;
import tea4life.recommendation_service.dto.event.OrderPlacedItemEvent;
import tea4life.recommendation_service.model.ProductAssociation;
import tea4life.recommendation_service.model.constant.RecommendationAssociationType;
import tea4life.recommendation_service.model.TrendingStat;
import tea4life.recommendation_service.model.UserPreference;
import tea4life.recommendation_service.repository.ProductAssociationRepository;
import tea4life.recommendation_service.repository.TrendingStatRepository;
import tea4life.recommendation_service.repository.UserPreferenceRepository;
import tea4life.recommendation_service.service.RecommendationStatsService;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecommendationStatsServiceImpl implements RecommendationStatsService {

    static final double ORDER_PREFERENCE_WEIGHT = 10.0;
    static final double PRODUCT_ASSOCIATION_WEIGHT = 5.0;
    static final double OPTION_VALUE_ASSOCIATION_WEIGHT = 3.0;
    static final double TRENDING_ORDER_WEIGHT = 10.0;

    UserPreferenceRepository userPreferenceRepository;
    ProductAssociationRepository productAssociationRepository;
    TrendingStatRepository trendingStatRepository;

    @Override
    public void handleOrderPlaced(OrderPlacedEvent event) {
        if (event == null || event.userId() == null || event.items() == null || event.items().isEmpty()) {
            return;
        }

        List<OrderPlacedItemEvent> items = event.items().stream()
                .filter(this::isValidItem)
                .toList();

        if (items.isEmpty()) {
            return;
        }

        for (OrderPlacedItemEvent item : items) {
            upsertUserPreference(event.userId(), item.categoryId(), quantityOrDefault(item));
            upsertTrendingStat(item.productId(), quantityOrDefault(item));
            upsertOptionValueAssociations(item.productId(), item.optionValueIds(), quantityOrDefault(item));
        }

        upsertProductAssociations(items);
    }

    private boolean isValidItem(OrderPlacedItemEvent item) {
        return item != null && item.productId() != null && item.categoryId() != null;
    }

    private void upsertUserPreference(Long userId, Long categoryId, int quantity) {
        UserPreference preference = userPreferenceRepository.findByUserIdAndCategoryId(userId, categoryId)
                .orElseGet(() -> {
                    UserPreference newPreference = new UserPreference();
                    newPreference.setUserId(userId);
                    newPreference.setCategoryId(categoryId);
                    newPreference.setPreferenceScore(0.0);
                    return newPreference;
                });

        preference.setPreferenceScore(preference.getPreferenceScore() + (quantity * ORDER_PREFERENCE_WEIGHT));
        preference.setLastUpdated(Instant.now());
        userPreferenceRepository.save(preference);
    }

    private void upsertTrendingStat(Long productId, int quantity) {
        TrendingStat stat = trendingStatRepository.findByProductIdAndStatDate(productId, LocalDate.now())
                .orElseGet(() -> {
                    TrendingStat newStat = new TrendingStat();
                    newStat.setProductId(productId);
                    newStat.setStatDate(LocalDate.now());
                    newStat.setViewCount(0L);
                    newStat.setOrderCount(0L);
                    newStat.setTotalScore(0.0);
                    return newStat;
                });

        stat.setOrderCount(stat.getOrderCount() + quantity);
        stat.setTotalScore(stat.getTotalScore() + (quantity * TRENDING_ORDER_WEIGHT));
        stat.setLastUpdated(Instant.now());
        trendingStatRepository.save(stat);
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
        ProductAssociation association = productAssociationRepository
                .findByProductIdAndAssociatedTargetIdAndAssociationType(productId, associatedTargetId, associationType)
                .orElseGet(() -> {
                    ProductAssociation newAssociation = new ProductAssociation();
                    newAssociation.setProductId(productId);
                    newAssociation.setAssociatedTargetId(associatedTargetId);
                    newAssociation.setAssociationType(associationType);
                    newAssociation.setCorrelationScore(0.0);
                    return newAssociation;
                });

        association.setCorrelationScore(association.getCorrelationScore() + increment);
        association.setLastUpdated(Instant.now());
        productAssociationRepository.save(association);
    }

    private int quantityOrDefault(OrderPlacedItemEvent item) {
        return item.quantity() == null || item.quantity() <= 0 ? 1 : item.quantity();
    }

    private List<Long> safeList(List<Long> values) {
        return values == null ? Collections.emptyList() : values;
    }
}
