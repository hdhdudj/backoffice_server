package io.spring.model.goods.request;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UploadFileRequestData {
	private String imageGb;

	@NotNull(message = "userId는 필수 값입니다.")
	private String userId;

}
