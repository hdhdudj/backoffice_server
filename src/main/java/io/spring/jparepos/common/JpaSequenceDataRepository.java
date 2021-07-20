package io.spring.jparepos.common;

import io.spring.model.common.entity.SequenceData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JpaSequenceDataRepository extends JpaRepository<SequenceData, String> {
    @Query(value = "select nextval(?1) as nextval", nativeQuery = true)
    String nextVal(String input);
}
