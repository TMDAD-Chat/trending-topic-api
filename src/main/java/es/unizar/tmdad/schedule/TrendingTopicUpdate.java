package es.unizar.tmdad.schedule;

import es.unizar.tmdad.service.BagOfWordsService;
import es.unizar.tmdad.service.MetricsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TrendingTopicUpdate {

    private final BagOfWordsService bagOfWordsService;
    private final MetricsService metricsService;

    public TrendingTopicUpdate(BagOfWordsService bagOfWordsService, MetricsService metricsService) {
        this.bagOfWordsService = bagOfWordsService;
        this.metricsService = metricsService;
    }

    @Scheduled(fixedRate = 30000)
    public void runScheduledTask(){
        log.info("Running TT scheduled tasks");
        var topWords = this.bagOfWordsService.getTopWordsOfToday();
        topWords.forEach(bagOfWordEntry -> metricsService.setMetricForTerm(bagOfWordEntry.getTerm(), bagOfWordEntry.getOccurrences()));
    }

}
