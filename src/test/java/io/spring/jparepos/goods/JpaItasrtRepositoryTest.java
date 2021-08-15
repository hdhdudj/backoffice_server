package io.spring.jparepos.goods;

import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
        Date a = null;
        try{
            a = new SimpleDateFormat("yyyy-MM-dd").parse("0000-01-01");
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(a);
        calendar.add(Calendar.HOUR_OF_DAY, 23);
        System.out.printf(a.toString());
        Date b = a;
    }
}