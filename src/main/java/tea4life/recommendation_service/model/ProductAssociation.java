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
import tea4life.recommendation_service.model.constant.RecommendationAssociationType;

import java.time.Instant;

@Document(collection = "product_associations")
@CompoundIndexes({
        @CompoundIndex(
                name = "uk_product_association_triplet",
                def = "{'product_id': 1, 'associated_target_id': 1, 'association_type': 1}",
                unique = true
        ),
        @CompoundIndex(name = "idx_product_association_lookup", def = "{'product_id': 1, 'association_type': 1, 'correlation_score': -1}"),
        @CompoundIndex(name = "idx_product_association_target", def = "{'associated_target_id': 1, 'association_type': 1}")
})
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductAssociation extends BaseEntity {

    @Id
    String id;

    @Field("product_id")
    Long productId;

    @Field("associated_target_id")
    Long associatedTargetId;

    @Field("association_type")
    RecommendationAssociationType associationType;

    @Field("correlation_score")
    Double correlationScore = 0.0;

    @Field("last_updated")
    Instant lastUpdated = Instant.now();
}
