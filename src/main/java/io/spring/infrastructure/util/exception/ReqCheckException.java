package io.spring.infrastructure.util.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ReqCheckException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ReqCheckException( String message) {
		super(message);
	  }
}
