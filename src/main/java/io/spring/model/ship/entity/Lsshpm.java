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
@Table(name = "lsshpm")
public class Lsshpm extends CommonProps {
    @Id
    private String shipId;
    private String shipOrderGb;
    private Long shipTimes;
    private String shipStatus;
    private Long deliId;
    private Long shipItemCnt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date receiptDt;
    private String storageId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date instructDt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date applyDay;
    private String masterShipGb;
    private String memo;
    private String siteGb;
    private String vendorId;
    private String delMethod;
    private String rematGb;
    private String shipGb;
    private String itemGrade;
    private String deliCompanyCd;
    private String orderId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date uploadDt;
    private String blNo;
}
