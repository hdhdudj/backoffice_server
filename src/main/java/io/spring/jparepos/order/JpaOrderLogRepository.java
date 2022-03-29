package io.spring.jparepos.order;

import io.spring.model.order.entity.OrderLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaOrderLogRepository extends JpaRepository<OrderLog, Long> {
}
