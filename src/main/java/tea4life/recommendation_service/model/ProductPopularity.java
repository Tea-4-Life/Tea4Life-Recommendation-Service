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

@Document(collection = "product_popularity")
@CompoundIndexes({
        @CompoundIndex(name = "uk_product_popularity_product", def = "{'product_id': 1}", unique = true),
        @CompoundIndex(name = "idx_product_popularity_score", def = "{'total_score': -1, 'last_updated': -1}")
})
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductPopularity extends BaseEntity {

    @Id
    String id;

    @Field("product_id")
    Long productId;

    @Field("view_count")
    Long viewCount = 0L;

    @Field("click_count")
    Long clickCount = 0L;

    @Field("order_count")
    Long orderCount = 0L;

    @Field("total_score")
    Double totalScore = 0.0;

    @Field("last_updated")
    Instant lastUpdated = Instant.now();
}
