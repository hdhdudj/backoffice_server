package io.spring.model.common.request;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonRequestData {
	@NotNull(message = "userId는 필수 값입니다.")
	private String userId;
}
