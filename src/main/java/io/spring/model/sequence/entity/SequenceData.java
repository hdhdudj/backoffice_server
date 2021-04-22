package io.spring.model.sequence.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Table(name = "sequence_data")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SequenceData {
    @Builder
    public SequenceData(String sequenceName, String sequenceCurValue)
    {
        this.sequenceName = sequenceName;
        this.sequenceCurValue = sequenceCurValue;
    }
    @Id
    private String sequenceName;

    private String sequenceIncrement;

    private String sequenceMinValue;
    private String sequenceMaxValue;
    private String sequenceCurValue;
    private String sequenceCycle;
}
