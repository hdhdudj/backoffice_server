package io.spring.jparepos.category;

import io.spring.model.category.entity.Itcate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JpaItcateRepository extends JpaRepository<Itcate, String> {
    @Query("select i from Itcate i order by i.categoryId asc")
    List<Itcate> selectAllItcate();
}
