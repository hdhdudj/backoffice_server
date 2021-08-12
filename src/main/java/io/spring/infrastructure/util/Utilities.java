package io.spring.infrastructure.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
//import org.flywaydb.core.internal.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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
    public static String getStringNo(char alphabet, String calcNeedStringNumber, int length){ // 들어온 string의 숫자는 정수여야 함
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
        calcRes = alphabet + calcRes;
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
     * 시간 없는 날짜에 시간을 추가해주는 함수
     */
    public static Date addHoursToJavaUtilDate(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }
}
