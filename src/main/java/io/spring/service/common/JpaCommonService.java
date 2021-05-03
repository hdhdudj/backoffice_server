package io.spring.service.common;

import io.spring.dao.common.MyBatisCommonDao;
import io.spring.jparepos.common.JpaSequenceDataRepository;
import io.spring.model.goods.request.GoodsInsertRequestData;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;

@Service
public class JpaCommonService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private JpaSequenceDataRepository jpaSequenceDataRepository;
    @Autowired
    private MyBatisCommonDao myBatisCommonDao;

	@Autowired
	private EntityManager em1;

//    private final String seqStr = "seq";
//    private final String seqNameStr = "seqName";
//    private final String nextvalStr = "nextval";

    public String getAssortId(@NotNull GoodsInsertRequestData goodsInsertRequestData, String sequenceName) {
		if (goodsInsertRequestData.getAssortId() != null && !goodsInsertRequestData.getAssortId().equals("")) { // 기존 리퀘스트에 assort id가 존재하는 경우 그대로 돌려보냄
            return goodsInsertRequestData.getAssortId();
        }
		// 기존 리퀘스트에 assort id가 존재하지 않는 경우
//        Itasrt itasrt = new Itasrt(goodsRequestData);
//        HashMap<String, Object> arr = new HashMap<String, Object>();
//
//        arr.put(seqNameStr, seqItasrtStr);
////        String res = jpaSequenceDataRepository.nextVal(seqItasrtStr);
////        System.out.println(res + "-------------------");
//
//		HashMap<String, Object> x1 = myBatisCommonDao.getSequence(arr); // max + 1 리턴
//        String assortId = StringUtils.leftPad(Long.toString((long)x1.get(nextvalStr)), 9, '0');
//        itasrt.setAssortId(assortId);
		Object r = em1.createNativeQuery("SELECT nextval('" + sequenceName + "')").getSingleResult(); // jpa로 부르기
        logger.debug("nextVal : ", r.toString());
        return StringUtils.leftPad(r.toString(), 9, '0');
    }


	public long getSequence(String seqName) {
		java.math.BigInteger r = (java.math.BigInteger) em1.createNativeQuery("SELECT nextval('" + seqName + "')")
				.getSingleResult();


		return r.longValue();
	}

}
