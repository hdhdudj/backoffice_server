package io.spring.model.goods.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class ProductsMasterPostRequestData {

	private Long masterId;

	private String masterNm;
	private String masterDescription;
	
	private String optionKey1;
	private String optionKey2;
	private String optionKey3;
	private String optionKey4;
	private String optionKey5;

	private String userId;



}
