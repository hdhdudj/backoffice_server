package io.spring.service.common;

import io.spring.dao.common.MyBatisCommonDao;
import io.spring.jparepos.common.JpaSequenceDataRepository;
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

    // 000012 식으로 반환
    public String getNumberId(@NotNull String id, String sequenceName, int size) {
		if (id != null && !id.equals("")) { // 기존 리퀘스트에 assort id가 존재하는 경우 그대로 돌려보냄
            return id;
        }
		Object r = em1.createNativeQuery("SELECT nextval('" + sequenceName + "')").getSingleResult(); // jpa로 부르기
        logger.debug("nextVal : ", r.toString());
        return StringUtils.leftPad(r.toString(), size, '0');
    }

    // C00001 식으로 반환
    public String getStrNumberId(String addStr, @NotNull String id, String sequenceName, int size){
        System.out.println("----------------------------- : " + id);
        if (id != null && !id.equals("")) { // 기존 리퀘스트에 purchaseNo가 존재하는 경우 그대로 돌려보냄
            return id;
        }
        Object r = em1.createNativeQuery("SELECT nextval('" + sequenceName + "')").getSingleResult(); // jpa로 부르기
        logger.debug("nextVal : ", r.toString());
        String returnStr = StringUtils.leftPad(r.toString(), size, '0');
        return addStr + returnStr;
    }


	public long getSequence(String seqName) {
		java.math.BigInteger r = (java.math.BigInteger) em1.createNativeQuery("SELECT nextval('" + seqName + "')")
				.getSingleResult();


		return r.longValue();
	}

}
