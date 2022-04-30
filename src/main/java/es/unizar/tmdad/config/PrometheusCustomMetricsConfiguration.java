package es.unizar.tmdad.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.atomic.AtomicInteger;

//@Configuration
public class PrometheusCustomMetricsConfiguration {

    private final Tags tags;

    public PrometheusCustomMetricsConfiguration(@Value("${spring.application.name}") String appName) {
        this.tags = Tags.of("service", appName);
    }

    @Bean
    public Counter incomingMessagesAmount(MeterRegistry meterRegistry){
        return meterRegistry.counter("trending_topics", tags);
    }

    @Bean
    public AtomicInteger trendingTopicsGauge(MeterRegistry meterRegistry){
        return meterRegistry.gauge("trending_topics", tags, new AtomicInteger(0));
    }
}
