package io.spring.jparepos.vendor;

import io.spring.model.vendor.entity.Cmvdmr;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaVendorRepository extends JpaRepository<Cmvdmr, String> {
}
