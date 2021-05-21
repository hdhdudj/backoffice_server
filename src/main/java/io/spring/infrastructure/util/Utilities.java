package io.spring.infrastructure.util;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.internal.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 21-05-03 Pecan
 * �쑀�떥 class : �뿬�윭 service�뿉�꽌 怨듯넻�쟻�쑝濡� �벐�씪 �렪�쓽 �븿�닔 紐⑥쓬 �겢�옒�뒪
 */
@Slf4j
public class Utilities {
    /**
     * 21-04-25 Pecan
     * �쑀�떥 �븿�닔 : "009"瑜� 諛쏆븘 �젙�닔�솕�빐�꽌 1�쓣 �뜑�븳 �썑 "010"�쑝濡� return
     * @param calcNeedStringNumber
     * @param length
     * @return String
     */
    public static String plusOne(String calcNeedStringNumber, int length){ // �뱾�뼱�삩 string�쓽 �닽�옄�뒗 �젙�닔�뿬�빞 �븿
        if(calcNeedStringNumber == null){
            return null;
        }
        String calcRes = "";
        try{
            calcRes = StringUtils.leftPad(Long.toString((long)Double.parseDouble(calcNeedStringNumber) + 1), length, '0');
        }
        catch(Exception e){
            log.debug(e.getMessage());
        }
        return calcRes;
    }

    /**
     * 21-05-04 Pecan
     * �쑀�떥 �븿�닔 : "9999-12-31 23:59:59"瑜� yyyy-MM-dd HH:mm:ss瑗� Date濡� 諛섑솚
     * @return Date
     */
    public static Date getStringToDate(String strDate){
        Date getDate = null;
        try{
            getDate = new SimpleDateFormat(StringFactory.getDateFormat()).parse(strDate);
        }
        catch(Exception e){
            log.debug(e.getMessage());
        }
        return getDate;
    }
    
    
    
}
