package org.intraportal.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class JobManagerException extends RuntimeException {

	public JobManagerException(String msg) {
		super(msg);
	}
}
