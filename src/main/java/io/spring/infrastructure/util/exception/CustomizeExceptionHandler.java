package io.spring.infrastructure.util.exception;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class CustomizeExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({InvalidRequestException.class})
    public ResponseEntity<Object> handleInvalidRequest(RuntimeException e, WebRequest request) {
        InvalidRequestException ire = (InvalidRequestException) e;

        List<FieldErrorResource> errorResources = ire.getErrors().getFieldErrors().stream().map(fieldError ->
            new FieldErrorResource(
                fieldError.getObjectName(),
                fieldError.getField(),
                fieldError.getCode(),
                fieldError.getDefaultMessage())).collect(Collectors.toList());

        ErrorResource error = new ErrorResource(errorResources);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return handleExceptionInternal(e, error, headers, UNPROCESSABLE_ENTITY, request);
    }

	@ExceptionHandler(value = TokenRefreshException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ErrorMessage handleTokenRefreshException(TokenRefreshException ex, WebRequest request) {
		return new ErrorMessage(HttpStatus.FORBIDDEN.value(), new Date(), ex.getMessage(),
				request.getDescription(false));
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		String errorMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
		List<String> validationList = ex.getBindingResult().getFieldErrors().stream()
				.map(fieldError -> fieldError.getDefaultMessage()).collect(Collectors.toList());
		System.out.println(validationList);
		String errMsg = "";
		for (String o : validationList) {
			if (errMsg.equals("")) {
				errMsg = o;
			} else {
				errMsg = errMsg + "^,^" + o;
			}

		}

		ErrorMessage err = new ErrorMessage(HttpStatus.BAD_REQUEST.value(), new Date(), errMsg,
				request.getDescription(false));
		
		
		return new ResponseEntity<>(err, status);
	}



}
