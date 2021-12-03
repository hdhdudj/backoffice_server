package io.spring.jparepos.goods;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        String strDate = localDateTime.format(formatter).toString();
        System.out.println(strDate.replace('T', ' '));
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
}