package io.spring.infrastructure.util;

import lombok.Getter;

public class StringFactory {
    // GoodsController
    @Getter
    private final static String seqItasrtStr = "seq_ITASRT";

    //jpaCommonService
    @Getter
    private final static String cUpperStr = "C";

    // jpaGoodsService
    @Getter
    private final static String threeStartCd = "001";
    @Getter
    private final static String fourStartCd = "0001";
    @Getter
    private final static String fiveStartCd = "00001";
    //    private final String nineStartCd = "000000001";
    @Getter
    private final static String gbOne = "01";
    @Getter
    private final static String gbTwo = "02";
    @Getter
    private final static String gbThree = "03";
    @Getter
    private final static String splitGb = "\\^\\|\\^";

    // number
    @Getter
    private final static String strOne = "1";
    @Getter
    private final static String strTwo = "2";

    // jpaPurchaseService
    @Getter
    private final static String purchaseSeqStr = "seq_LSPCHM";
    @Getter
    private final static String depositPlanId = "seq_LSDPSP";
    @Getter
    private final static String ninetyNine = "99";
    @Getter
    private final static int intNine = 9;
    @Getter
    private final static int intEight = 8;
    @Getter
    private final static String strZero = "0";
    @Getter
    private final static String strStartDt = "startDt";
    @Getter
    private final static String strEndDt = "endDt";
    @Getter
    private final static String strPurchaseVendorId = "purchaseVendorId";
    @Getter
    private final static String strAssortId = "assortId";
    @Getter
    private final static String strPurchaseStatus = "purchaseStatus";


    // controller
    @Getter
    private final static String strOk = "ok";
    @Getter
    private final static String strSuccess = "success";

    @Getter
    private final static String doomDay = "9999-12-31 23:59:59";
    @Getter
    private final static String dateFormat = "yyyy-MM-dd HH:mm:ss";

    // deposit controller
    @Getter
    private final static String strDepositNo = "seq_LSDPSM";
    @Getter
    private final static String dUpperStr = "D";

    // itvari 관련
    @Getter
    private final static String strSingleGoods = "단품";

    // code
    @Getter
    private final static String strR = "r";

    // 기호
    @Getter
    private final static String strAt = "@";
    
    // 주문 상태코드
    @Getter
    private final static String strA01 = "A01"; // 주문접수
    @Getter
    private final static String strA02 = "A02"; // 주문확인
    @Getter
    private final static String strB01 = "B01"; // 발주대기
    @Getter
    private final static String strB02 = "B02"; // 발주완료
    @Getter
    private final static String strC01 = "C01"; // 해외입고완료 (수입에만 존재)
    @Getter
    private final static String strC02 = "C02"; // 이동지시 (수입에만 존재)
    @Getter
    private final static String strC03 = "C03"; // 이동지시완료 (수입에만 존재)
    @Getter
    private final static String strC04 = "C04"; // 국내(현지)입고완료
    @Getter
    private final static String strD01 = "D01"; // 출고지시
    @Getter
    private final static String strD02 = "D02"; // 출고
    @Getter
    private final static String strD03 = "D03"; // 국제운송
    @Getter
    private final static String strD04 = "D04"; // 통관
    @Getter
    private final static String strD05 = "D05"; // 국내운송
    @Getter
    private final static String strD06 = "D06"; // 배송완료
    @Getter
    private final static String strE01 = "E01"; // 구매확정
}
