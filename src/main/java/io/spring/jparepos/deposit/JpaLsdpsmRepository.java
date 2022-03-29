package io.spring.jparepos.deposit;

import org.springframework.data.jpa.repository.JpaRepository;

import io.spring.model.deposit.entity.Lsdpsm;

public interface JpaLsdpsmRepository extends JpaRepository<Lsdpsm, String> {
    Lsdpsm findByDepositNo(String depositNo);

	Lsdpsm findByDepositNoAndDepositGb(String depositNo, String depositGb);

}
