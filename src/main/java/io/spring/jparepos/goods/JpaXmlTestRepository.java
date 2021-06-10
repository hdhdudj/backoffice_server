package io.spring.jparepos.goods;

import io.spring.model.goods.entity.XmlTest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaXmlTestRepository extends JpaRepository<XmlTest, String> {
}
