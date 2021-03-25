package io.spring.infrastructure.util;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponseMessageWithTuiGrid<T> {

	// HttpStatus
	private Boolean result;
	private String message;
	private T data;

	@Builder
	public ApiResponseMessageWithTuiGrid(Boolean result, String message, T data) {
		this.result = result;
		this.message = message;
		this.data = data;
	}

}
