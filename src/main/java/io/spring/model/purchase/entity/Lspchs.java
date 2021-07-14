package io.spring.model.purchase.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.model.common.entity.CommonProps;
import io.spring.model.purchase.request.PurchaseInsertRequestData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name="lspchs")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lspchs extends CommonProps {
    private final static Logger logger = LoggerFactory.getLogger(Lspchs.class);
    public Lspchs(PurchaseInsertRequestData purchaseInsertRequestData){
        this.purchaseNo = purchaseInsertRequestData.getPurchaseNo();
        this.effEndDt = Utilities.getStringToDate(StringFactory.getDoomDay());
        this.purchaseStatus = purchaseInsertRequestData.getPurchaseStatus();
    }
    public Lspchs(Lspchs lspchs){
        this.purchaseNo = lspchs.getPurchaseNo();
        this.effEndDt = Utilities.getStringToDate(StringFactory.getDoomDay());
        this.purchaseStatus = lspchs.getPurchaseStatus();
        this.effStaDt = lspchs.getEffStaDt();
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;
    private String purchaseNo;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date effEndDt;
    private String purchaseStatus;
    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date effStaDt;
}
