package io.spring.model.move.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 이동리스트 조회용 DTO
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MoveCompletedLIstReponseData {
    public MoveCompletedLIstReponseData(LocalDate startDt, LocalDate endDt, String shipId, String assortId, String assortNm, String storeCd){
        this.startDt = startDt;
        this.endDt = endDt;
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
    private List<Move> moves;

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Move{
        private LocalDateTime shipDt;
        private LocalDateTime shipIndDt;
        private String shipId;
        private String shipSeq;
        private String shipKey;
        private String trackNo; // lsshpm.blNo
        private String storageId;
        private String oStorageId;
        private String shipGb;
        private String deliMethod;
        private String assortId;
        private String itemId;
        private String goodsKey;
        private String assortNm;
        private String optionNm1;
        private String optionNm2;
        private Long qty;
    }
}
