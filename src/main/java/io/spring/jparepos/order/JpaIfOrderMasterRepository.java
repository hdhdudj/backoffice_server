package io.spring.jparepos.order;

import io.spring.model.order.entity.IfOrderMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaIfOrderMasterRepository extends JpaRepository<IfOrderMaster, String> {
    IfOrderMaster findByChannelOrderNo(String ifNo);

    List<IfOrderMaster> findByIfStatus(String ifStatus);
}
