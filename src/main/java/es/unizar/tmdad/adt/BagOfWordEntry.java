package es.unizar.tmdad.adt;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BagOfWordEntry {

    private String term;
    private Integer occurrences;
    private Double logLikelihood;

}
