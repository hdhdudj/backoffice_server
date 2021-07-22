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

@Getter
@Entity
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "lsshps")
public class Lsshps extends CommonProps {
    @Id
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
