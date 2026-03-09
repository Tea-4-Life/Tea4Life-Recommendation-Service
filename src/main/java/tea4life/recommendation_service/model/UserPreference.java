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

@Entity
@Table(
        name = "user_preferences",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_preference_user_category", columnNames = {"user_id", "category_id"})
        },
        indexes = {
                @Index(name = "idx_user_preference_user_score", columnList = "user_id, preference_score"),
                @Index(name = "idx_user_preference_category", columnList = "category_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserPreference extends BaseEntity {

    @Id
    @SnowflakeGenerated
    Long id;

    @Column(name = "user_id", nullable = false)
    Long userId;

    @Column(name = "category_id", nullable = false)
    Long categoryId;

    @Column(name = "preference_score", nullable = false)
    Double preferenceScore = 0.0;

    @Column(name = "last_updated", nullable = false)
    Instant lastUpdated = Instant.now();
}
