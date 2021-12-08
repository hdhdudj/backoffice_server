package io.spring.infrastructure.util;

import io.spring.enums.DeliveryMethod;
import io.spring.model.common.SetOptionInterface;
import io.spring.model.goods.entity.Itvari;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collector;
import java.util.stream.Collectors;

//import org.flywaydb.core.internal.util.StringUtils;

/**
 * 21-05-03 Pecan
 * 유틸 class : 여러 service에서 공통적으로 쓰일 편의 함수 모음 클래스
 */
@Slf4j
public class Utilities {
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
            log.debug(e.getMessage());
        }
        return calcRes;
    }
    /**
     * 21-04-25 Pecan
     * 유틸 함수 : char가 앞에 붙은 숫자 반환. (예 : C00000001 )
     * @param calcNeedStringNumber
     * @param length
     * @return String
     */
    public static String getStringNo(Character alphabet, String calcNeedStringNumber, int length){ // 들어온 string의 숫자는 정수여야 함
        if(calcNeedStringNumber == null){
            return null;
        }
        String calcRes = "";
        try{
            calcRes = StringUtils.leftPad(Long.toString((long)Double.parseDouble(calcNeedStringNumber)), length - 1, '0');
        }
        catch(Exception e){
            log.debug(e.getMessage());
        }
        if(alphabet != null){
            calcRes = alphabet + calcRes;
        }
        return calcRes;
    }

    /**
     * 21-05-04 Pecan
     * 유틸 함수 : "9999-12-31 23:59:59"를 yyyy-MM-dd HH:mm:ss꼴 Date로 반환
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

    /**
     * 21-06-16 Pecan
     * stream 사용시 list로 collect할 때 list의 원소 개수가 1개면 해당 객체를 반환해주는 함수
     * @param <T>
     * @return
     */
    public static <T> Collector<T, ?, T> toSingleton() {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    if(list.size() == 0){
//                        System.out.println("list.size() : " + list.size());
                        return null;
                    }
                    else if (list.size() > 1) {
//                        System.out.println("list.size() : " + list.size());
                        throw new IllegalStateException();
                    }
//                    System.out.println("list.size() : " + list.size());
                    return list.get(0);
                }
        );
    }

    /**
     * String 날짜를 받아서 LocalDateTime으로 변환해 반환하는 함수
     * @param strDt
     * @return LocalDateTime
     */
    public static LocalDateTime strToLocalDateTime(String strDt){
        LocalDateTime date = LocalDateTime.parse(strDt, DateTimeFormatter.ISO_DATE_TIME);
        return date;
    }

    /**
     * 두 String 사이에 -를 넣어서 붙여서 반환하는 함수 (a,b) -> "a-b"
     */
    public static String addDashInMiddle(String a, String b){
        if(a == null || b == null){
            return "";
        }
        return a + StringFactory.getStrDash() + b;
    }

    /**
     * LocalDateTime을 받아서 T를 뗀 String(1111-11-11 11:11:11 꼴)을 반환하는 함수
     * @return String
     */
    public static String removeTAndTransToStr(LocalDateTime localDateTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        String strDate = localDateTime.format(formatter);
        return strDate.replace('T', ' ');
    }

    /**
     * Date를 localDateTime으로 변환
     * @param date
     * @return
     */
    public static LocalDateTime dateToLocalDateTime(Date date){
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * LocalDateTime to Date
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime){
        return java.sql.Timestamp.valueOf(localDateTime);
    }

    /**
     * optionNm1, optionNm2, optionNm3 설정 함수
     */
    public static void setOptionNames(SetOptionInterface setOptionInterface, List<Itvari> itvariList){
        if(itvariList == null){
            log.debug("itvari list가 존재하지 않습니다.");
            return;
        }
        if(itvariList.size() > 0){
            setOptionInterface.setOptionNm1(itvariList.get(0) == null? "" : itvariList.get(0).getOptionNm());
        }
        if(itvariList.size() > 1){
            setOptionInterface.setOptionNm2(itvariList.get(1) == null? "" : itvariList.get(1).getOptionNm());
        }
        if(itvariList.size() > 2){
            setOptionInterface.setOptionNm3(itvariList.get(2) == null? "" : itvariList.get(2).getOptionNm());
        }
    }

    /**
     * Date to String
     */
    public static String dateToString(Date from){
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String to = transFormat.format(from);
        return to;
    }

    /**
     *  MyBatis로 결과물을 받았을 때 해당 prop이 null이면 빈 칸으로 바꿔주는 함수
     */
    public static void changeNullToEmpty(HashMap<String, Object> map){
        for(String key : map.keySet()){
            if(map.get(key) == null){
                map.put(key, "");
            }
        }
    }

    public static String convertFieldNameToEnum(String arg){
        if(arg == null){
            return null;
        }
        for(DeliveryMethod d : DeliveryMethod.values()){
            if(d.getFieldName().equals(arg)){
                return d.toString();
            }
        }
        return null;
    }
}
