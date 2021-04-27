package io.spring.model.goods.idclass;

import io.spring.model.goods.GoodsRequestData;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItitmdId implements Serializable {
    private static final long serialVersionUID = 1L;
    private final static Logger logger = LoggerFactory.getLogger(ItitmdId.class);
    public ItitmdId(GoodsRequestData goodsRequestData, String itemId){
        this.assortId = goodsRequestData.getAssortId();
        this.itemId = itemId;
        try
        {
            this.effEndDt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("9999-12-31 23:59:59"); // 마지막 날짜(없을 경우 9999-12-31 23:59:59?)
        }
        catch (Exception e){
            logger.debug(e.getMessage());
        }
        this.effStaDt = new Date();// 오늘날짜
    }
    private String assortId;
    private String itemId;
    private Date effEndDt;
    private Date effStaDt;
}
