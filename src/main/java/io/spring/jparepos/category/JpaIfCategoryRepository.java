package io.spring.jparepos.category;

import io.spring.model.goods.entity.IfCategory;
import io.spring.model.goods.idclass.IfCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaIfCategoryRepository extends JpaRepository<IfCategory, IfCategoryId> {
    IfCategory findByChannelGbAndChannelCategoryId(String channelGb, String channelCategoryId);

    IfCategory findByChannelGbAndCategoryId(String channelGb, String categoryId);
}
