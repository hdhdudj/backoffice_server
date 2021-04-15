package io.spring.data.goods;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoodsRequestData {
	private long assortId;
	private String regDt;
	private String regId;
	private String updId;
	private String updDt;
}
