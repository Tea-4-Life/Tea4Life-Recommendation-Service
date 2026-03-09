package tea4life.recommendation_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(
        name = "trending_stats",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_trending_stats_product_date", columnNames = {"product_id", "stat_date"})
        },
        indexes = {
                @Index(name = "idx_trending_stats_date_score", columnList = "stat_date, total_score"),
                @Index(name = "idx_trending_stats_product_date", columnList = "product_id, stat_date")
        }
)
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrendingStat extends BaseEntity {

    @Id
    @SnowflakeGenerated
    Long id;

    @Column(name = "product_id", nullable = false)
    Long productId;

    @Column(name = "stat_date", nullable = false)
    LocalDate statDate;

    @Column(name = "view_count", nullable = false)
    Long viewCount = 0L;

    @Column(name = "order_count", nullable = false)
    Long orderCount = 0L;

    @Column(name = "total_score", nullable = false)
    Double totalScore = 0.0;

    @Column(name = "last_updated", nullable = false)
    Instant lastUpdated = Instant.now();
}
