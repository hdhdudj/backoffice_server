package io.spring.model.purchase.request;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PrintDtRequestData {

	private String purchaseNo;
	private String printDt;
	@NotNull(message = "userId는 필수 값입니다.")
	private String userId;
}
