package io.spring.jparepos.goods;

import io.spring.model.goods.GoodsRequestData;
import io.spring.model.goods.entity.Itasrt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JpaItasrtRepository extends JpaRepository<Itasrt, String>{
    @Query("select i.assortId, i.assortNm, i.shortageYn, i.brandId, ib.brandId, i.dispCategoryId, ic.categoryId  from Itasrt i, Itbrnd ib, Itcatg ic")
    List<Itasrt> getGoodsList(GoodsRequestData goodsRequestData);
}

//select
//        i.assort_id
//        ,i.assort_nm
//        ,i.shortage_yn
//        ,i.brand_id
//        ,ib.brand_nm
//        ,i.disp_category_id
//        ,ic.category_nm
//        from itasrt i
//        ,itbrnd ib
//        ,itcatg ic
//        where 1=1
//        and i.brand_id = ib.brand_id
//        and i.disp_category_id  = ic.category_id
//        and i.reg_Dt between '2021-01-01 00:00:01' and  '2021-04-30 00:00:01'
//        and i.shortage_yn ='01'
