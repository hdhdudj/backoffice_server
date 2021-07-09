package io.spring.jparepos.purchase;

import io.spring.model.purchase.entity.Lspchm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface JpaLspchmRepository extends JpaRepository<Lspchm, String> {
    Optional<Lspchm> findByPurchaseNo(String purchaseNo);

    @Query(value = "select nextval('seq_LSPCHM') as nextval", nativeQuery = true)
    String findMaxPurchaseNo();
//    @Query("select m from Lspchm m join fetch m.lspchdList")
//    List<Lspchm> findPurchaseList(HashMap<String, Object> param);
}
