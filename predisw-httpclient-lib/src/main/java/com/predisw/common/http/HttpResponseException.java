package com.predisw.common.http;

public class HttpResponseException extends RuntimeException {

  private int statusCode;

  public HttpResponseException(int statusCode, Throwable cause) {
    super(cause);
    this.statusCode = statusCode;
  }

  public HttpResponseException(int statusCode, String message, Throwable cause) {
    super(message, cause);
    this.statusCode = statusCode;
  }

  public int getStatusCode() {
    return statusCode;
  }
}
