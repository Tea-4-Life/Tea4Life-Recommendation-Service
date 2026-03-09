package tea4life.recommendation_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import tea4life.recommendation_service.config.database.SnowflakeGenerated;
import tea4life.recommendation_service.model.base.BaseEntity;
import tea4life.recommendation_service.model.constant.RecommendationAssociationType;

import java.time.Instant;

@Entity
@Table(
        name = "product_associations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_product_association_triplet",
                        columnNames = {"product_id", "associated_target_id", "association_type"}
                )
        },
        indexes = {
                @Index(name = "idx_product_association_lookup", columnList = "product_id, association_type, correlation_score"),
                @Index(name = "idx_product_association_target", columnList = "associated_target_id, association_type")
        }
)
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductAssociation extends BaseEntity {

    @Id
    @SnowflakeGenerated
    Long id;

    @Column(name = "product_id", nullable = false)
    Long productId;

    @Column(name = "associated_target_id", nullable = false)
    Long associatedTargetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "association_type", nullable = false, length = 32)
    RecommendationAssociationType associationType;

    @Column(name = "correlation_score", nullable = false)
    Double correlationScore = 0.0;

    @Column(name = "last_updated", nullable = false)
    Instant lastUpdated = Instant.now();
}
