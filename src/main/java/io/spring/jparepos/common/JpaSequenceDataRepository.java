package io.spring.jparepos.common;

import io.spring.model.common.entity.SequenceData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;

public interface JpaSequenceDataRepository extends JpaRepository<SequenceData, String> {
    @Procedure(procedureName = "nextval")
    String nextVal(String input);
}
