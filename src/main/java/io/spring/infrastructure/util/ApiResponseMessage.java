package io.spring.infrastructure.util;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponseMessage<T> {

	// HttpStatus
	private String code;
	// Http Default Message
	private String message;
	private T data;

	@Builder
	public ApiResponseMessage(String code, String message, T data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}

}
