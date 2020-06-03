package br.com.jpb.email.exception;

import lombok.Getter;

public class EmailProviderException extends RuntimeException {

	@Getter
	private final int statusCode;

	public EmailProviderException(String message, int statusCode) {
		super(message);
		this.statusCode = statusCode;
	}

	public EmailProviderException(String message, Throwable cause, int statusCode) {
		super(message, cause);
		this.statusCode = statusCode;
	}
}
