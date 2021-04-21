package io.spring.dao.goods.jparep;

import io.spring.model.goods.entity.Ititmm;
import io.spring.model.goods.idclass.ItitmmId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaItitmmRepository  extends JpaRepository<Ititmm, ItitmmId> {
}
