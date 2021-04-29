package io.spring.jparepos.goods;

import io.spring.model.goods.entity.Itasrt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface JpaItasrtRepository extends JpaRepository<Itasrt, String>{
    @Query(value = "select i.assortId, i.assortNm, i.shortageYn, i.brandId, ib.brandNm, i.dispCategoryId, ic.categoryNm from Itasrt i, Itbrnd ib, Itcatg ic where 1=1 and i.brandId = ib.brandId and i.dispCategoryId = ic.categoryId and i.shortageYn = ?1 and i.regDt between ?2 and ?3")
    List<Object[]> getGoodsList(String shortageYn, Date regDtBegin, Date regDtEnd);
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
