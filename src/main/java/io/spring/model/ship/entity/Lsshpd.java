package io.spring.model.ship.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.spring.model.common.entity.CommonProps;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="lsshpd")
public class Lsshpd extends CommonProps {
    @Id
    private String shipId;
    @Id
    private String shipSeq;
    private String assortId;
    private String itemId;
    private String shipVendorId;
    private Long shipIndicateQty;
    private Long shipQty;
    private String vendorDealCd;
    private String vatGb;
    private String orderId;
    private String orderSeq;
    private String shipGb;
    private String siteGb;
    private String vendorId;
    private String rackNumber;
    private Float customsTax;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date excAppDt;
    private Float orderDiscount;
    private Float saleCost;
    private Float localPrice;
    private Float localDeliFee;
    private Float localTax;
    private Float disPrice;
    private String oStorageId;
}
