package tea4life.recommendation_service.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import tea4life.recommendation_service.model.base.BaseEntity;

import java.time.Instant;

@Document(collection = "user_preferences")
@CompoundIndexes({
        @CompoundIndex(name = "uk_user_preference_user_category", def = "{'user_id': 1, 'category_id': 1}", unique = true),
        @CompoundIndex(name = "idx_user_preference_user_score", def = "{'user_id': 1, 'preference_score': -1}"),
        @CompoundIndex(name = "idx_user_preference_category", def = "{'category_id': 1}")
})
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserPreference extends BaseEntity {

    @Id
    String id;

    @Field("user_id")
    Long userId;

    @Field("category_id")
    Long categoryId;

    @Field("preference_score")
    Double preferenceScore = 0.0;

    @Field("last_updated")
    Instant lastUpdated = Instant.now();
}
