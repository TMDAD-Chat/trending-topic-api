package es.unizar.tmdad.service.impl;

import es.unizar.tmdad.service.MetricsService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MetricsServiceImpl implements MetricsService {

    private final MeterRegistry meterRegistry;
    private final String appName;

    private final Map<String, AtomicInteger> currentGauges = new HashMap<>();

    public MetricsServiceImpl(MeterRegistry meterRegistry, @Value("${spring.application.name}") String appName) {
        this.meterRegistry = meterRegistry;
        this.appName = appName;
    }

    @Override
    public void setMetricForTerm(String term, Integer occurrences) {
        if(this.currentGauges.containsKey(term)){
            this.currentGauges.get(term).set(occurrences);
        }else {
            this.currentGauges.put(term,
                    meterRegistry.gauge("trending_topics",
                            Tags.of("service", appName, "term", term, "occurrences", String.valueOf(occurrences)),
                            new AtomicInteger(occurrences)));
        }
    }
}
