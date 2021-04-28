package io.spring.model.goods.entity;

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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ititmd {
    private final static Logger logger = LoggerFactory.getLogger(Ititmd.class);
    public Ititmd(Ititmm ititmm){
        this.assortId = ititmm.getAssortId();
        this.itemId = ititmm.getItemId();
        try
        {
            this.effEndDt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("9999-12-31 23:59:59"); // 마지막 날짜(없을 경우 9999-12-31 23:59:59?)
        }
        catch (Exception e){
            logger.debug(e.getMessage());
        }
        this.effStaDt = new Date();// 오늘날짜
        this.shortYn = ititmm.getShortYn();
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    private String assortId;
    private String itemId;
    private Date effEndDt;
    private Date effStaDt;
    private String shortYn;

    @Column(nullable = true)
    private Long regId;
    @Column(nullable = true)
    private Long updId;
    @CreationTimestamp
    private Date regDt;
    @UpdateTimestamp
    private Date updDt;
}
