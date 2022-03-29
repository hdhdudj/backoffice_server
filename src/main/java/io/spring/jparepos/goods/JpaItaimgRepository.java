package io.spring.jparepos.goods;

import io.spring.model.goods.entity.Itaimg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaItaimgRepository extends JpaRepository<Itaimg, Long> {
	
	List<Itaimg> findByAssortIdAndImageGb(String assortId,String imageGb);
	List<Itaimg> findByAssortId(String assortId);
}
