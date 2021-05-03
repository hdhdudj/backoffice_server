package io.spring.infrastructure.util;

import lombok.Getter;

public class StringFactory {
    // GoodsController
    @Getter
    private final static String seqItasrtStr = "seq_ITASRT";

    // jpaGoodsService
    @Getter
    private final static String threeStartCd = "001";
    @Getter
    private final static String fourStartCd = "0001";
    //    private final String nineStartCd = "000000001";
    @Getter
    private final static String gbOne = "01";
    @Getter
    private final static String gbTwo = "02";
    @Getter
    private final static String splitGb = "\\^\\|\\^";

}
