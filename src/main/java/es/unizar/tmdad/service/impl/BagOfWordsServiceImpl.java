package es.unizar.tmdad.service.impl;

import es.unizar.tmdad.adt.BagOfWordEntry;
import es.unizar.tmdad.repository.BagOfWordsRepository;
import es.unizar.tmdad.repository.entity.BagOfWordsTermEntity;
import es.unizar.tmdad.service.BagOfWordsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BagOfWordsServiceImpl implements BagOfWordsService {
    private final long offset = 86400000;

    private final BagOfWordsRepository bagOfWordsRepository;

    public BagOfWordsServiceImpl(BagOfWordsRepository bagOfWordsRepository) {
        this.bagOfWordsRepository = bagOfWordsRepository;
    }

    @Override
    @Transactional
    public void addWordsToBag(List<String> terms, Date timestamp) {
        final Date dateTimestamp = new Date(removeTrailingTime(timestamp.getTime()));
        Map<String, Integer> occurrences =
            terms.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(String::toUpperCase, s -> 1, Integer::sum));

        occurrences.forEach((key, value) -> this.bagOfWordsRepository.updateTermOccurrences(key, value, dateTimestamp));

        occurrences.forEach((key, value) -> this.calculateLogLikelihoodRatio(key, value, dateTimestamp));

        List<String> termsToUpdate = this.bagOfWordsRepository.findAllByTermDate(dateTimestamp)
                .stream()
                .map(BagOfWordsTermEntity::getTerm)
                .collect(Collectors.toList());

        //REVIEW the rest of the terms of the current day as the denominator is now bigger, so the likelihood has changed
        termsToUpdate.forEach(term -> this.calculateLogLikelihoodRatio(term, 0, dateTimestamp));

    }

    private long removeTrailingTime(long time) {
        return offset*(time / offset);
    }

    public List<BagOfWordEntry> getTopWordsOfToday(){
        final Date dateTimestamp = new Date(removeTrailingTime(System.currentTimeMillis()));
        var terms = this.bagOfWordsRepository.getTop15ByTermDateOrderByLogLikelihoodDesc(dateTimestamp);
        return terms.stream()
                .map(e -> BagOfWordEntry.builder()
                        .term(e.getTerm())
                        .occurrences(e.getOccurrences())
                        .logLikelihood(e.getLogLikelihood())
                        .build()
                )
                .collect(Collectors.toList());
    }

    private void calculateLogLikelihoodRatio(String term, Integer newOcurrences, Date timestamp){
        Date yesterday = new Date(timestamp.getTime() - offset);
        Optional<BagOfWordsTermEntity> termEntityNow = bagOfWordsRepository.findById(BagOfWordsTermEntity.BagOfWordsCompositeKey.builder().term(term).termDate(timestamp).build());
        Optional<BagOfWordsTermEntity> termEntityPreviousDay = bagOfWordsRepository.findById(BagOfWordsTermEntity.BagOfWordsCompositeKey.builder().term(term).termDate(yesterday).build());

        double occurrencesOfNow = (termEntityNow.isPresent() ? termEntityNow.get().getOccurrences() : 0);
        double occurrencesOfPreviousDay = termEntityPreviousDay.isPresent() ? termEntityPreviousDay.get().getOccurrences() : 0;

        double amountOfWordsNow = bagOfWordsRepository.countAllByTermDate(timestamp);
        double amountOfWordsPreviousDay = bagOfWordsRepository.countAllByTermDate(yesterday);

        double percentageNow = occurrencesOfNow / amountOfWordsNow;
        double percentagePreviousDay = occurrencesOfPreviousDay / amountOfWordsPreviousDay;
        double percentageBothDays = (occurrencesOfNow + occurrencesOfPreviousDay) / (amountOfWordsNow + amountOfWordsPreviousDay);

        double logLikelihood = 2*(logLikelihood(percentageNow, occurrencesOfNow, amountOfWordsNow) + logLikelihood(percentagePreviousDay, occurrencesOfPreviousDay, amountOfWordsPreviousDay) -
                logLikelihood(percentageBothDays, occurrencesOfNow, amountOfWordsNow) - logLikelihood(percentageBothDays, occurrencesOfPreviousDay, amountOfWordsPreviousDay));
        if(percentageNow < percentagePreviousDay){
            logLikelihood *= -1;
        }

        BagOfWordsTermEntity updatedEntity;
        if(termEntityNow.isEmpty()){
            updatedEntity = new BagOfWordsTermEntity(term, timestamp, newOcurrences, logLikelihood);
        }else{
            updatedEntity = termEntityNow.get();
            updatedEntity.setLogLikelihood(logLikelihood);
        }

        bagOfWordsRepository.save(updatedEntity);
    }

    private double logLikelihood(double percentage, double occurrences, double amount){
        return (occurrences == 0 ? 0 : Math.log(percentage)) +
                ((amount-occurrences) == 0 ? 0 : (amount-occurrences)*Math.log(1-percentage));
    }
}
