package io.spring.jparepos.goods;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.spring.model.goods.entity.Itaimg;
import io.spring.model.goods.entity.Ititmd;

public interface JpaItaimgRepository extends JpaRepository<Itaimg, Long> {
	
	List<Itaimg> findByAssortIdAndImageGb(String assortId,String imageGb);
	

}
