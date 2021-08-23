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
}