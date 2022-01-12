package io.spring.jparepos.goods;

import io.spring.model.goods.entity.IfBrand;
import io.spring.model.goods.idclass.IfBrandId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface JpaIfBrandRepository extends JpaRepository<IfBrand, IfBrandId> {
    IfBrand findByChannelGbAndChannelBrandId(String channelGb, String brandId);

    IfBrand findByChannelGbAndBrandId(String gbOne, String brandId);

    @Query("select ib from IfBrand ib where ib.channelGb=?1 and ib.brandId in ?2")
    List<IfBrand> findByBrandIdListByChannelIdAndBrandIdList(String gbOne, List<String> brandIdList);
}
