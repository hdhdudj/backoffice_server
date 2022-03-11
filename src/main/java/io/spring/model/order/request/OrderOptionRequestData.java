package io.spring.model.order.request;

import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class OrderOptionRequestData {
	private String orderId;
	private String orderSeq;
	private String assortId;
	private String itemId;
	private String channelGoodsNo;
	private String channelOptionSno;
	@NotNull(message = "userId는 필수 값입니다.")
	private String userId;

}

//=CONCATENATE("{",h3,i3,j3,m3,k3,l3,"}")