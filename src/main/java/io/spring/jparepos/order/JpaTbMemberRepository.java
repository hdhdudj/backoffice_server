package io.spring.jparepos.order;

import io.spring.model.order.entity.TbMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaTbMemberRepository extends JpaRepository<TbMember, Long> {
    TbMember findByLoginId(String loginId);
}
