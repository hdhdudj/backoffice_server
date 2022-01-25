package io.spring.model.goods.entity;

import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.model.common.entity.CommonProps;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name="ititmd")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ititmd extends CommonProps {
    private final static Logger logger = LoggerFactory.getLogger(Ititmd.class);
    public Ititmd(Ititmd ititmd){
        this.assortId = ititmd.getAssortId();
        this.shortYn = ititmd.getShortYn();
        this.itemId = ititmd.getItemId();
        this.effEndDt = Utilities.strToLocalDateTime(StringFactory.getDoomDayT()); // 마지막 날짜(없을 경우 9999-12-31 23:59:59?)
    }
    public Ititmd(Ititmm ititmm){
        this.assortId = ititmm.getAssortId();
        this.itemId = ititmm.getItemId();
        this.shortYn = ititmm.getShortYn();
        this.effEndDt = Utilities.strToLocalDateTime(StringFactory.getDoomDayT());
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    private String assortId;
    private String itemId;
    private LocalDateTime effEndDt;
    @CreationTimestamp
    private LocalDateTime effStaDt;
    private String shortYn;
}
