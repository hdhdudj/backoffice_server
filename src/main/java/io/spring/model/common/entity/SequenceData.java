package io.spring.model.common.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@NamedStoredProcedureQuery(name = "nextval",
        procedureName = "nextval",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "seqName", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "seq", type = String.class),
        })
public class SequenceData implements Serializable {
    @Id
    private String sequenceName;
    private String sequenceIncrement;
    private String sequenceMinValue;
    private String sequenceMaxValue;
    private String sequenceCurValue;
    private String sequenceCycle;
}
