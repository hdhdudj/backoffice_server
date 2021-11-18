package io.spring.jparepos.goods;

import io.spring.model.goods.entity.IfBrand;
import io.spring.model.goods.idclass.IfBrandId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaIfBrandRepository extends JpaRepository<IfBrand, IfBrandId> {
    IfBrand findByChannelGbAndChannelBrandId(String channelGb, String brandId);

    IfBrand findByChannelGbAndBrandId(String gbOne, String brandId);
}
