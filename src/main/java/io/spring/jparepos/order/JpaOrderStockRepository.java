package io.spring.jparepos.order;

import org.springframework.data.jpa.repository.JpaRepository;

import io.spring.model.order.entity.OrderStock;

public interface JpaOrderStockRepository extends JpaRepository<OrderStock, Long> {

}
