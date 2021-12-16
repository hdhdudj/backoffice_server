package io.spring.model.move.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import io.spring.infrastructure.util.Utilities;
import io.spring.model.ship.entity.Lsshpd;
import io.spring.model.ship.entity.Lsshpm;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * 이동처리 조회 DTO
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MoveListSaveData {
    public MoveListSaveData(LocalDate startDt, LocalDate endDt, String shipId, String assortId, String assortNm, String storeCd, String deliMethod){
        this.startDt = startDt;
        this.endDt = endDt;
        this.shipId = shipId;
        this.assortId = assortId;
        this.assortNm = assortNm;
        this.storageId = storeCd;
        this.deliMethod = deliMethod;
    }
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDt;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDt;
    private String shipId;
    private String assortId;
    private String assortNm;
    private String storageId;
    private String deliMethod;
    private List<Move> moves;

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Move{
        public Move(Lsshpm lsshpm, Lsshpd lsshpd){
            this.moveIndDt = Utilities.localDateTimeToDate(lsshpm.getReceiptDt());
            this.shipId = lsshpd.getShipId();
            this.shipSeq = lsshpd.getShipSeq();
            this.shipKey = Utilities.addDashInMiddle(shipId, shipSeq);
            this.shipGb = lsshpd.getShipGb();
            this.deliMethod = lsshpm.getDelMethod();
            this.trackNo = lsshpm.getBlNo();
            this.assortId = lsshpd.getAssortId();
            this.itemId = lsshpd.getItemId();
            this.goodsKey = Utilities.addDashInMiddle(assortId,itemId);
            this.assortNm = lsshpd.getItasrt().getAssortNm();
            // 옵션은 바깥에서 set
            this.qty = lsshpd.getShipIndicateQty();
        }
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private Date moveIndDt;
        private String shipId;
        private String shipSeq;
        private String shipKey;
        private String shipGb;
        private String deliMethod;
        private String trackNo;
        private String assortId;
        private String itemId;
        private String goodsKey;
        private String assortNm;
        private String optionNm1;
        private String optionNm2;
        private String optionNm3;
        private Long qty;
    }
}
