package io.spring.jparepos.order;

import org.springframework.data.jpa.repository.JpaRepository;

import io.spring.model.order.entity.TbOrderMaster;

public interface JpaTbOrderMasterRepository extends JpaRepository<TbOrderMaster, String> {

}
