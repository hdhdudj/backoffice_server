package io.spring.service.common;

import io.spring.dao.common.MyBatisCommonDao;
import io.spring.jparepos.common.JpaSequenceDataRepository;
import io.spring.jparepos.common.JpaTestenum2Repository;
import io.spring.jparepos.deposit.JpaLsdpdsRepository;
import io.spring.jparepos.deposit.JpaLsdpsdRepository;
import io.spring.jparepos.deposit.JpaLsdpsmRepository;
import io.spring.jparepos.deposit.JpaLsdpspRepository;
import io.spring.jparepos.deposit.JpaLsdpssRepository;
import io.spring.jparepos.goods.JpaItitmcRepository;
import io.spring.jparepos.goods.JpaItitmtRepository;
import io.spring.model.common.entity.Testenum2;
import io.spring.model.deposit.entity.Lsdpsm;
import io.spring.model.deposit.request.DepositInsertRequestData;
import io.spring.service.deposit.JpaDepositService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;


@Slf4j
@Service
@RequiredArgsConstructor
public class JpaCommonService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private JpaSequenceDataRepository jpaSequenceDataRepository;
    @Autowired
    private MyBatisCommonDao myBatisCommonDao;

    @Autowired
    private JpaTestenum2Repository jpaTestenum2Repository;  
    
	@Autowired
	private EntityManager em1;

//    private final String seqStr = "seq";
//    private final String seqNameStr = "seqName";
//    private final String nextvalStr = "nextval";

    // 000012 �떇�쑝濡� 諛섑솚
	
	@Transactional
	public Testenum2 saveTestEnum2(Testenum2 p){
		jpaTestenum2Repository.save(p);
	        return p;
	    }
	
    public String getNumberId(@NotNull String id, String sequenceName, int size) {
		if (id != null && !id.equals("")) { // 湲곗〈 由ы�섏뒪�듃�뿉 assort id媛� 議댁옱�븯�뒗 寃쎌슦 洹몃�濡� �룎�젮蹂대깂
            return id;
        }
		Object r = em1.createNativeQuery("SELECT nextval('" + sequenceName + "')").getSingleResult(); // jpa濡� 遺�瑜닿린
        logger.debug("nextVal : ", r.toString());
        return StringUtils.leftPad(r.toString(), size, '0');
    }

    // C00001 �떇�쑝濡� 諛섑솚
    public String getStrNumberId(String addStr, @NotNull String id, String sequenceName, int size){
//        System.out.println("----------------------------- : " + id);
        if (id != null && !id.equals("")) { // 湲곗〈 由ы�섏뒪�듃�뿉 purchaseNo媛� 議댁옱�븯�뒗 寃쎌슦 洹몃�濡� �룎�젮蹂대깂
            return id;
        }
        Object r = em1.createNativeQuery("SELECT nextval('" + sequenceName + "')").getSingleResult(); // jpa濡� 遺�瑜닿린
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
