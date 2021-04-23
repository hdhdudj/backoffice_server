package io.spring.service.common;

import io.spring.dao.common.MyBatisCommonDao;
import io.spring.jparepos.common.JpaSequenceDataRepository;
import io.spring.model.goods.GoodsRequestData;
import io.spring.model.goods.entity.Itasrt;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.HashMap;

@Service
public class JpaCommonService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private JpaSequenceDataRepository jpaSequenceDataRepository;
    @Autowired
    private MyBatisCommonDao myBatisCommonDao;

    private final String seqStr = "seq";
    private final String seqItasrtStr = "seq_ITASRT";
    private final String seqNameStr = "seqName";
    private final String nextvalStr = "nextval";

    public String getAssortId(@NotNull GoodsRequestData goodsRequestData) {
        if (goodsRequestData.getAssortId() != null && !goodsRequestData.getAssortId().equals("")) { // 기존 리퀘스트에 assort id가 존재하는 경우 그대로 돌려보냄
            return goodsRequestData.getAssortId();
        }
        // 기존 리퀘스트에 assort id가 존재하지 않는 경우
        Itasrt itasrt = new Itasrt(goodsRequestData);
        HashMap<String, Object> arr = new HashMap<String, Object>();

        arr.put(seqNameStr, seqItasrtStr);
//        String res = jpaSequenceDataRepository.nextVal(seqItasrtStr);
//        System.out.println(res + "-------------------");

        HashMap<String, Object> x1 = myBatisCommonDao.getSequence(arr); // max + 1 해서 옴
        logger.debug("nextVal : ", x1.get(nextvalStr));
        String assortId = StringUtils.leftPad(Long.toString((long)x1.get(nextvalStr)), 9, '0');
        itasrt.setAssortId(assortId);
        return assortId;
    }

}
