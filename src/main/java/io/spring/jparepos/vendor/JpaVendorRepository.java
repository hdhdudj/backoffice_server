package io.spring.jparepos.vendor;

import io.spring.model.vendor.entity.Cmvdmr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JpaVendorRepository extends JpaRepository<Cmvdmr, String> {
    @Query("select max(c.id) from Cmvdmr c")
    String findMaxId();
}
