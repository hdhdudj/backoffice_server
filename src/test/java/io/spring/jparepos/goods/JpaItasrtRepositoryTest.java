package io.spring.jparepos.goods;

import io.spring.enums.DeliveryMethod;
import io.spring.infrastructure.util.Utilities;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

class JpaItasrtRepositoryTest {

    @Test
    public void initTables(){
        try {
            String hibernateVersion = org.hibernate.annotations.common.Version.getVersionString();
            System.out.println("Hibernate Version: "+ hibernateVersion);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void dateTest(){
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        String strDate = localDateTime.format(formatter);
        System.out.println(strDate);
        System.out.println(strDate.replace('T', ' '));
    }

    @Test
    public void dateTest2(){
        LocalDateTime ldt = LocalDateTime.of(2021, 12, 01, 10, 24, 00);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        String strDate = ldt.format(formatter);
        String strDate2 = LocalDateTime.parse("2021-12-01 10:24:00", formatter).toString();
        System.out.println(strDate);
        System.out.println(strDate2);
    }

    /**
     * mapstruct 어노테이션 인쇄 노가다용
     */
    @Test
    public void printNullCheckMappingAnnotation(){
        String to = "purchaseId purchaseDt ownerId purchaseRemark storageId terms dealtypeCd delivery payment carrier siteOrderNo purchaseStatus";
        String[] strs = to.split(" ");
        String inputValNm = "p";
        for(String s : strs){

            String method = "get" + s.substring(0,1).toUpperCase() + s.substring(1, s.length());
            String a = "\"java("+inputValNm+"."+method+"() == null? \\\"\\\" : "+inputValNm+"."+method+"())\"";
            String b = "@Mapping(target = \""+s+"\", expression = "+a+")";
            System.out.println(b);
        }
    }

    @Test
    public void enumTest(){
        String arg = "001";
        for(DeliveryMethod d : DeliveryMethod.values()){
            if(d.getFieldName().equals(arg)){
                System.out.println(d);
            }
        }
    }
}