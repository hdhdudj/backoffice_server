package io.spring.model.deposit.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.model.common.entity.CommonProps;
import io.spring.model.deposit.request.DepositInsertRequestData;
import io.spring.model.deposit.response.DepositListWithPurchaseInfoData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="lsdpds")
public class Lsdpds extends CommonProps implements Serializable {
    public Lsdpds(String depositNo, DepositInsertRequestData.Item item){
        this.depositNo = depositNo;
        this.depositSeq = item.getDepositSeq();
        this.effEndDt = Utilities.getStringToDate(StringFactory.getDoomDay()); // 9999-12-31 하드코딩
        this.effStaDt = new Date();
        this.depositStatus = item.getDepositStatus();
    }
    public Lsdpds(Lsdpsd lsdpsd){
        this.depositNo = lsdpsd.getDepositNo();
        this.depositSeq = lsdpsd.getDepositSeq();
        this.effEndDt = Utilities.getStringToDate(StringFactory.getDoomDay()); // 9999-12-31 하드코딩
        this.effStaDt = new Date();
        this.depositStatus = StringFactory.getGbOne(); // 01 하드코딩
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;
    private String depositNo;
    private String depositSeq;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date effEndDt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date effStaDt;
    private String depositStatus;
}
