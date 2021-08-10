package io.spring.jparepos.deposit;

import io.spring.model.deposit.entity.Lsdpss;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface JpaLsdpssRepository extends JpaRepository<Lsdpss, Long> {
    Lsdpss findByDepositNoAndEffEndDt(String depositNo, Date stringToDate);
}
