package io.spring.jparepos.deposit;

import io.spring.model.deposit.entity.Lsdpsd;
import io.spring.model.deposit.idclass.LsdpsdId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JpaLsdpsdRepository extends JpaRepository<Lsdpsd, LsdpsdId> {
    @Query("select max(d.depositSeq) from Lsdpsd d where d.depositNo = ?1")
    String findMaxDepositSeqByDepositNo(String depositNo);

    List<Lsdpsd> findByDepositNo(String depositNo);
}
