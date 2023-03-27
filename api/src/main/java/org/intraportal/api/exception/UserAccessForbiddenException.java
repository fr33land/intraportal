package org.intraportal.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "User access forbidden")
public class UserAccessForbiddenException extends RuntimeException {

	public UserAccessForbiddenException() {
		super();
	}

	public UserAccessForbiddenException(String msg) {
		super(msg);
	}

}
