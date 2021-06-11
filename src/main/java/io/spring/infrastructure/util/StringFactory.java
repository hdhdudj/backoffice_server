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
    private final static String splitGb = "\\^\\|\\^";

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

}
