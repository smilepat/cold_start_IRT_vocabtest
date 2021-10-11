package com.marvrus.vocabularytest.config.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;

import java.nio.charset.Charset;

public class ApiException extends HttpStatusCodeException {
    public ApiException(HttpStatus statusCode) {
        super(statusCode);
    }

    public ApiException(HttpStatus statusCode, String statusText) {
        super(statusText, statusCode, statusText, null, null, null);
    }

    public ApiException(HttpStatus statusCode, String statusText, byte[] responseBody, Charset responseCharset) {
        super(statusCode, statusText, responseBody, responseCharset);
    }

    public ApiException(String message, HttpStatus statusCode, String statusText, HttpHeaders responseHeaders,
                        byte[] responseBody, Charset responseCharset) {
        super(message, statusCode, statusText, responseHeaders, responseBody, responseCharset);
    }

    public ApiException(HttpStatus statusCode, String statusText,
                        @Nullable HttpHeaders responseHeaders, @Nullable byte[] responseBody,
                        @Nullable Charset responseCharset) {

        super(statusText, statusCode, statusText, responseHeaders, responseBody, responseCharset);
    }
}
