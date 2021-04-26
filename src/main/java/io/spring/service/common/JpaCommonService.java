package io.spring.service.common;

import java.util.HashMap;

import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.spring.dao.common.MyBatisCommonDao;
import io.spring.jparepos.common.JpaSequenceDataRepository;
import io.spring.model.goods.GoodsRequestData;
import io.spring.model.goods.entity.Itasrt;

@Service
public class JpaCommonService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private JpaSequenceDataRepository jpaSequenceDataRepository;
    @Autowired
    private MyBatisCommonDao myBatisCommonDao;

	@Autowired
	private EntityManager em1;

    private final String seqStr = "seq";
    private final String seqItasrtStr = "seq_ITASRT";
    private final String seqNameStr = "seqName";
    private final String nextvalStr = "nextval";

    public String getAssortId(@NotNull GoodsRequestData goodsRequestData) {
		if (goodsRequestData.getAssortId() != null && !goodsRequestData.getAssortId().equals("")) { // 湲곗〈 由ы�섏뒪�듃�뿉
																									// assort id媛�
																									// 議댁옱�븯�뒗 寃쎌슦 洹몃�濡�
																									// �룎�젮蹂대깂
            return goodsRequestData.getAssortId();
        }
		// 湲곗〈 由ы�섏뒪�듃�뿉 assort id媛� 議댁옱�븯吏� �븡�뒗 寃쎌슦
        Itasrt itasrt = new Itasrt(goodsRequestData);
        HashMap<String, Object> arr = new HashMap<String, Object>();

        arr.put(seqNameStr, seqItasrtStr);
//        String res = jpaSequenceDataRepository.nextVal(seqItasrtStr);
//        System.out.println(res + "-------------------");

		HashMap<String, Object> x1 = myBatisCommonDao.getSequence(arr); // max + 1 �빐�꽌 �샂
        logger.debug("nextVal : ", x1.get(nextvalStr));
        String assortId = StringUtils.leftPad(Long.toString((long)x1.get(nextvalStr)), 9, '0');
        itasrt.setAssortId(assortId);
		Object r = em1.createNativeQuery("SELECT nextval('seq_ITASRT')").getSingleResult();
        return assortId;
    }


	public long getSequence(String seqName) {
		java.math.BigInteger r = (java.math.BigInteger) em1.createNativeQuery("SELECT nextval('" + seqName + "')")
				.getSingleResult();


		return r.longValue();
	}

}
