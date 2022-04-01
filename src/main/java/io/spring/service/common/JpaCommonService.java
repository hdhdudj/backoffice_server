package io.spring.service.common;

import java.util.List;

import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.spring.dao.common.MyBatisCommonDao;
import io.spring.jparepos.common.JpaSequenceDataRepository;
import io.spring.jparepos.common.JpaSuppliersRepository;
import io.spring.jparepos.common.JpaTestenum2Repository;
import io.spring.model.common.entity.Suppliers;
import io.spring.model.common.entity.Testenum2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class JpaCommonService {
    private final JpaSequenceDataRepository jpaSequenceDataRepository;
    private final MyBatisCommonDao myBatisCommonDao;
    private final JpaTestenum2Repository jpaTestenum2Repository;  
	private final EntityManager em1;
	private final JpaSuppliersRepository jpaSuppliersRepository;

//    private final String seqStr = "seq";
//    private final String seqNameStr = "seqName";
//    private final String nextvalStr = "nextval";

    // 000012
	
	@Transactional
	public Testenum2 saveTestEnum2(Testenum2 p){
		jpaTestenum2Repository.save(p);
	        return p;
	    }
	
		public List<Suppliers> getAllSuppliers() {

			List<Suppliers> r = jpaSuppliersRepository.findAll();
			return r;
			
		}

    public String getNumberId(@NotNull String id, String sequenceName, int size) {
		if (id != null && !id.equals("")) {
            return id;
        }
		Object r = em1.createNativeQuery("SELECT nextval('" + sequenceName + "')").getSingleResult();
        log.debug("nextVal : ", r.toString());
        return StringUtils.leftPad(r.toString(), size, '0');
    }

    // C00001
    public String getStrNumberId(String addStr, @NotNull String id, String sequenceName, int size){
//        System.out.println("----------------------------- : " + id);
        if (id != null && !id.equals("")) {
            return id;
        }
        Object r = em1.createNativeQuery("SELECT nextval('" + sequenceName + "')").getSingleResult();
        log.debug("nextVal : ", r.toString());
        String returnStr = StringUtils.leftPad(r.toString(), size, '0');
        return addStr + returnStr;
    }


	public long getSequence(String seqName) {
		java.math.BigInteger r = (java.math.BigInteger) em1.createNativeQuery("SELECT nextval('" + seqName + "')")
				.getSingleResult();


		return r.longValue();
	}
}
