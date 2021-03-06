package io.spring.model.move.response;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import io.spring.infrastructure.util.Utilities;
import io.spring.model.common.SetOptionInterface;
import io.spring.model.ship.entity.Lsshpd;
import io.spring.model.ship.entity.Lsshpm;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 이동처리 조회 DTO
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MoveListResponseData {
    public MoveListResponseData(LocalDate startDt, LocalDate endDt, String shipId, String assortId, String assortNm, String storageId, String deliMethod){
        this.startDt = startDt;
        this.endDt = endDt;
        this.shipId = shipId;
        this.assortId = assortId;
        this.assortNm = assortNm;
        this.storageId = storageId;
        this.deliMethod = deliMethod;
    }
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDt;
    @JsonSerialize(using = LocalDateSerializer.class)
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
    public static class Move implements SetOptionInterface {
        public Move(Lsshpm lsshpm, Lsshpd lsshpd){
            this.moveIndDt = Utilities.removeTAndTransToStr(lsshpm.getReceiptDt());
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
			this.rackNo = lsshpd.getRackNo();

			this.optionNm1 = lsshpd.getItitmm().getItvari1() == null ? ""
					: lsshpd.getItitmm().getItvari1().getOptionNm();
			this.optionNm2 = lsshpd.getItitmm().getItvari2() == null ? ""
					: lsshpd.getItitmm().getItvari2().getOptionNm();
			this.optionNm3 = lsshpd.getItitmm().getItvari3() == null ? ""
					: lsshpd.getItitmm().getItvari3().getOptionNm();

        }
        private String moveIndDt;
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
		private String rackNo;
    }
}
