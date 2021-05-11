package io.spring.model.deposit.entity;

import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.model.deposit.request.DepositInsertRequestData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="lsdpds")
public class Lsdpds {
    public Lsdpds(String depositNo, DepositInsertRequestData.Item item){
        this.depositNo = depositNo;
        this.effEndDt = Utilities.getStringToDate(StringFactory.getDoomDay());
        this.effStaDt = new Date();
        this.depositStatus = item.getDepositStatus();
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;
    private String depositNo;
    private String depositSeq;
    private Date effEndDt;
    private Date effStaDt;
    private String depositStatus;
    private Long regId;
    @CreationTimestamp
    private Date regDt;
    private Long updId;
    @UpdateTimestamp
    private Date updDt;
}
