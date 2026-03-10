package tea4life.recommendation_service.scheduler;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tea4life.recommendation_service.service.RecommendationStatsService;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecommendationDecayScheduler {

    RecommendationStatsService recommendationStatsService;

    @Scheduled(cron = "${recommendation.popularity.decay-cron:0 0 0 1 * *}")
    public void applyDecay() {
        log.info("Running monthly popularity decay");
        recommendationStatsService.applyPopularityDecay();
    }
}
