package io.spring.model.ship.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.model.common.entity.CommonProps;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Entity
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "lsshps")
public class Lsshps extends CommonProps {
    public Lsshps(Lsshpm lsshpm){
        this.shipId = lsshpm.getShipId();
        this.effEndDt = Utilities.getStringToDate(StringFactory.getDoomDay()); // 9999-12-31 하드코딩
        this.effStaDt = new Date();
        this.shipStatus = lsshpm.getShipStatus();
//        this.shipIndicateUserid =
//        this.shipUserid =
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String seq;
    private String shipId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date effEndDt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date effStaDt;
    private String shipStatus;
    private Long shipIndicateUserid;
    private Long shipUserid;
}
