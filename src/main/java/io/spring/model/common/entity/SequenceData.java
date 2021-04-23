package io.spring.model.common.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SequenceData {
    @Id
    private String sequenceName;
    private String sequenceIncrement;
    private String sequenceMinValue;
    private String sequenceMaxValue;
    private String sequenceCurValue;
    private String sequenceCycle;
}
