package io.spring.jparepos.order;

import io.spring.model.order.entity.TbMemberAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaTbMemberAddressRepository extends JpaRepository<TbMemberAddress, Long> {
    TbMemberAddress findByCustId(Long custId);
}
