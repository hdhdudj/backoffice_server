package io.spring.model.deposit.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.model.common.entity.CommonProps;
import io.spring.model.deposit.request.DepositInsertRequestData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    public Lsdpss(Lsdpsm lsdpsm) {
        this.depositNo = lsdpsm.getDepositNo();
        this.effEndDt = Utilities.getStringToDate(StringFactory.getDoomDay());
        this.depositStatus = StringFactory.getGbOne(); // 01 하드코딩
        this.setRegId(lsdpsm.getRegId());
        this.setUpdId(lsdpsm.getUpdId());
    }

	public Lsdpss(Lsdpsm lsdpsm, String depositStatus) {
		this.depositNo = lsdpsm.getDepositNo();
		this.effEndDt = Utilities.getStringToDate(StringFactory.getDoomDay());
		this.depositStatus = depositStatus;
		this.setRegId(lsdpsm.getRegId());
		this.setUpdId(lsdpsm.getUpdId());
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
