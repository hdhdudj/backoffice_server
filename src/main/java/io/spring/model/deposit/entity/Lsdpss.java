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
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name="lsdpss")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lsdpss extends CommonProps {
    public Lsdpss(DepositInsertRequestData depositInsertRequestData){
        this.depositNo = depositInsertRequestData.getDepositNo();
        this.effEndDt = Utilities.getStringToDate(StringFactory.getDoomDay());
        this.depositStatus = depositInsertRequestData.getDepositStatus();
        this.effStaDt = new Date();
    }
    public Lsdpss(Lsdpsm lsdpsm, DepositListWithPurchaseInfoData.Deposit deposit) {
        this.depositNo = lsdpsm.getDepositNo();
        this.effEndDt = Utilities.getStringToDate(StringFactory.getDoomDay());
        this.depositStatus = StringFactory.getGbOne(); // 01 하드코딩
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;
    private String depositNo;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date effEndDt;
    private String depositStatus;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    @CreationTimestamp
    private Date effStaDt;

}
