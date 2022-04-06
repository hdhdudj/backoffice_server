package io.spring.model.purchase.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.spring.infrastructure.util.StringFactory;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@Setter
@Table(name="lspchs")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lspchs extends CommonProps {
    private final static Logger logger = LoggerFactory.getLogger(Lspchs.class);
    public Lspchs(PurchaseInsertRequestData purchaseInsertRequestData){
        this.purchaseNo = purchaseInsertRequestData.getPurchaseNo();
		this.effEndDt = LocalDateTime.parse(StringFactory.getDoomDay(),
				DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.purchaseStatus = purchaseInsertRequestData.getPurchaseStatus();

        this.setRegId(purchaseInsertRequestData.getUserId());
        this.setUpdId(purchaseInsertRequestData.getUserId());
    }
    public Lspchs(Lspchs lspchs){
        this.purchaseNo = lspchs.getPurchaseNo();
		this.effEndDt = LocalDateTime.parse(StringFactory.getDoomDay(),
				DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.purchaseStatus = lspchs.getPurchaseStatus();
        this.effStaDt = lspchs.getEffStaDt();
    }
    public Lspchs(Lspchm lspchm, String regId){
        this.purchaseNo = lspchm.getPurchaseNo();
		this.effEndDt = LocalDateTime.parse(StringFactory.getDoomDay(),
				DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.purchaseStatus = lspchm.getPurchaseStatus();
        this.effStaDt = lspchm.getPurchaseDt();

        super.setRegId(regId);
        super.setUpdId(regId);
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;
    private String purchaseNo;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime effEndDt;
    private String purchaseStatus;
    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime effStaDt;
}
