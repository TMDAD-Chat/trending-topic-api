package es.unizar.tmdad.repository;

import es.unizar.tmdad.repository.entity.BagOfWordsTermEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.Date;

public interface BagOfWordsRepository extends CrudRepository<BagOfWordsTermEntity, BagOfWordsTermEntity.BagOfWordsCompositeKey> {

    Double countAllByTermDate(Date termDate);

    @Modifying
    @Query("UPDATE terms b SET b.occurrences = b.occurrences + :occurrences WHERE b.term = :term AND b.termDate = :timestamp")
    void updateTermOccurrences(String term, Integer occurrences, Date timestamp);

    Collection<BagOfWordsTermEntity> getTop15ByTermDateOrderByLogLikelihoodDesc(Date termDate);

    Collection<BagOfWordsTermEntity> findAllByTermDate(Date termDate);
}
