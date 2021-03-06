package io.spring.jparepos.deposit;

import io.spring.model.deposit.entity.Lsdpds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

public interface JpaLsdpdsRepository extends JpaRepository<Lsdpds, String> {
    @Query("select max(s.depositSeq) from Lsdpsd s where s.depositNo=?1")
    String findMaxDepositSeqByDepositNo(String depositNo);

    Lsdpds findByDepositNoAndDepositSeqAndEffEndDt(String depositNo, String depositSeq, Date stringToDate);
}
