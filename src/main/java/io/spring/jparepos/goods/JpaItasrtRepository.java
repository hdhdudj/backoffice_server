package io.spring.jparepos.goods;

import io.spring.model.goods.entity.Itasrt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JpaItasrtRepository extends JpaRepository<Itasrt, String>{
    @Query("select i from Itasrt i join fetch Itvari v where i.assortId=?1")
    Itasrt findByAssortId(String assortId);
}
