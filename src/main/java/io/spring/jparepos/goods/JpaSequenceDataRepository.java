package io.spring.jparepos.goods;

import io.spring.model.sequence.entity.SequenceData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaSequenceDataRepository extends JpaRepository<SequenceData, String> {
}
