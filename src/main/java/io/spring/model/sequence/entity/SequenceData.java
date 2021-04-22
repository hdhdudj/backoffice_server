package io.spring.model.sequence.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Table(name = "sequence_data")
public class SequenceData {
    @Id
    private String sequenceName;

    private String sequenceIncrement;

    private String sequenceMinValue;
    private String sequenceMaxValue;
    private String sequenceCurValue;
    private String sequenceCycle;
}
