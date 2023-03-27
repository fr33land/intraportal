package org.intraportal.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Validation exception.")
public class ValidationResultException extends RuntimeException {
	private static final long serialVersionUID = -3625480216917544803L;

	public ValidationResultException() {
		super();
	}

	public ValidationResultException(String message) {
		super(message);
	}
}
