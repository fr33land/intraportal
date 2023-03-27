package org.intraportal.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Please insert a valid start date and end date.")
public class DateValidationException extends RuntimeException {
	private static final long serialVersionUID = -3625480216917544803L;

	public DateValidationException() {
		super();
	}

	public DateValidationException(String message) {
		super(message);
	}
}
