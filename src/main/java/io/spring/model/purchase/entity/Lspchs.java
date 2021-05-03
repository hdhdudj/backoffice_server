package io.spring.model.purchase.entity;

import io.spring.model.purchase.request.PurchaseInsertRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name="lspchs")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lspchs {
    private final static Logger logger = LoggerFactory.getLogger(Lspchs.class);
    public Lspchs(PurchaseInsertRequest purchaseInsertRequest){
        this.purchaseNo = purchaseInsertRequest.getPurchaseNo();
        try{
            this.effEndDt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("9999-12-31 23:59:59");
        }
        catch (Exception e){
            logger.debug(e.getMessage());
        }
        this.purchaseStatus = purchaseInsertRequest.getPurchaseStatus();
    }
    public Lspchs(Lspchs lspchs){
        this.purchaseNo = lspchs.getPurchaseNo();
        try{
            this.effEndDt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("9999-12-31 23:59:59");
        }
        catch (Exception e){
            logger.debug(e.getMessage());
        }
        this.purchaseStatus = lspchs.getPurchaseStatus();
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;
    private String purchaseNo;
    private Date effEndDt;
    private String purchaseStatus;
    @CreationTimestamp
    private Date effStaDt;
    private Long regId;
    @CreationTimestamp
    private Date regDt;
    private Long updId;
    @UpdateTimestamp
    private Date updDt;
}
