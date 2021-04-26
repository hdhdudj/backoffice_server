package io.spring.jparepos.common;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;

import io.spring.model.common.entity.SequenceData;

public interface JpaSequenceDataRepository extends JpaRepository<SequenceData, String> {
    @Procedure(procedureName = "nextval")
    String nextVal(String input);

}
