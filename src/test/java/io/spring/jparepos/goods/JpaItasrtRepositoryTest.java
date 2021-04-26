package io.spring.jparepos.goods;

import org.junit.jupiter.api.Test;

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
}