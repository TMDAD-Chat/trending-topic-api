package es.unizar.tmdad.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "terms")
@IdClass(BagOfWordsTermEntity.BagOfWordsCompositeKey.class)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BagOfWordsTermEntity {

    @Id
    private String term;
    @Id
    private Date termDate;

    @Column
    private Integer occurrences;

    @Column
    private Double logLikelihood;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class BagOfWordsCompositeKey implements Serializable {
        private String term;
        private Date termDate;
    }
}
