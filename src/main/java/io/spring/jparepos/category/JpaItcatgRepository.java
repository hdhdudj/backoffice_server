package io.spring.jparepos.category;

import io.spring.model.goods.entity.Itcatg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaItcatgRepository extends JpaRepository<Itcatg, String> {
    List<Itcatg> findByUpCategoryId(String categoryId);
}
