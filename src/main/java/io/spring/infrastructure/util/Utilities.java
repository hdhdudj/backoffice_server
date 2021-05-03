package io.spring.infrastructure.util;

import org.flywaydb.core.internal.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 21-05-03 Pecan
 * 유틸 class : 여러 service에서 공통적으로 쓰일 편의 함수 모음 클래스
 */
public class Utilities {
    private static Logger logger = LoggerFactory.getLogger(Utilities.class);
    /**
     * 21-04-25 Pecan
     * 유틸 함수 : "009"를 받아 정수화해서 1을 더한 후 "010"으로 return
     * @param calcNeedStringNumber
     * @param length
     * @return String
     */
    public static String plusOne(String calcNeedStringNumber, int length){ // 들어온 string의 숫자는 정수여야 함
        if(calcNeedStringNumber == null){
            return null;
        }
        String calcRes = "";
        try{
            calcRes = StringUtils.leftPad(Long.toString((long)Double.parseDouble(calcNeedStringNumber) + 1), length, '0');
        }
        catch(Exception e){
            logger.debug(e.getMessage());
        }
        return calcRes;
    }
}
