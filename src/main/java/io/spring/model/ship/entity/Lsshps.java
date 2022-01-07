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
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Entity
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "lsshps")
public class Lsshps extends CommonProps {
    public Lsshps(Lsshpm lsshpm, String userId){
        this.shipId = lsshpm.getShipId();
        this.effEndDt = Utilities.strToLocalDateTime2(StringFactory.getDoomDay()); // 9999-12-31 하드코딩
        this.effStaDt = LocalDateTime.now();
        this.shipStatus = lsshpm.getShipStatus();
//        this.shipIndicateUserid =
//        this.shipUserid =
        super.setRegId(userId);
        super.setUpdId(userId);
    }
    public Lsshps(Lsshpm lsshpm){
        this.shipId = lsshpm.getShipId();
        this.effEndDt = Utilities.strToLocalDateTime2(StringFactory.getDoomDay()); // 9999-12-31 하드코딩
        this.effStaDt = LocalDateTime.now();
        this.shipStatus = lsshpm.getShipStatus();
//        this.shipIndicateUserid =
//        this.shipUserid =
        super.setRegId(lsshpm.getUpdId());
        super.setUpdId(lsshpm.getUpdId());
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String seq;
    private String shipId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime effEndDt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime effStaDt;
    private String shipStatus;
    private Long shipIndicateUserid;
    private Long shipUserid;
}
