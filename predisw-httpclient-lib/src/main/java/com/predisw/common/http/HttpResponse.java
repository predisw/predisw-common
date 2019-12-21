package com.predisw.common.http;

import okhttp3.Headers;
import okhttp3.Protocol;

public class HttpResponse {
  private Protocol protocol;
  private int statusCode;
  private String body;
  private Headers headers;

  private HttpResponse() {}

  private HttpResponse(Builder builder) {
    this.protocol = builder.response.protocol;
    this.statusCode = builder.response.statusCode;
    this.body = builder.response.body;
    this.headers = builder.response.headers;
  }

  @Override
  public String toString() {
    return "HttpResponse{"
        + "protocol="
        + protocol
        + ", statusCode="
        + statusCode
        + ", body='"
        + body
        + '\''
        + ", headers="
        + headers
        + '}';
  }

  public Protocol getProtocol() {
    return protocol;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public String getBody() {
    return body;
  }

  public Headers getHeaders() {
    return headers;
  }

  public static class Builder {
    private HttpResponse response;

    public Builder() {
      response = new HttpResponse();
    }

    public Builder protocol(Protocol protocol) {
      response.protocol = protocol;
      return this;
    }

    public Builder statusCode(int statusCode) {
      response.statusCode = statusCode;
      return this;
    }

    public Builder body(String body) {
      response.body = body;
      return this;
    }

    public Builder headers(Headers headers) {
      response.headers = headers;
      return this;
    }

    public HttpResponse build() {
      return new HttpResponse(this);
    }
  }
}
