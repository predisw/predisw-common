package com.predisw.common.http;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.Map;
import java.util.Objects;

public class HttpRequest {
  private String url;
  private String body;
  private Multimap<String, String> headers = ArrayListMultimap.create();
  private String mediaType = "application/json; charset=utf-8";

  private Builder builder;

  private HttpRequest() {};

  private HttpRequest(Builder builder) {
    this.url = builder.httpRequest.url;
    this.body = builder.httpRequest.body;
    this.mediaType = builder.httpRequest.mediaType;
    this.headers = builder.httpRequest.headers;
    this.builder = builder;
  }

  public Builder builder() {
    return builder;
  }

  public String getMediaType() {
    return mediaType;
  }

  public String getUrl() {
    return url;
  }

  public String getBody() {
    return body;
  }

  public Multimap<String, String> getHeaders() {
    return ImmutableMultimap.copyOf(headers);
  }

  @Override
  public String toString() {
    return "HttpRequest{"
        + "url='"
        + url
        + '\''
        + ", body='"
        + body
        + '\''
        + ", headers="
        + headers
        + ", mediaType='"
        + mediaType
        + '\''
        + '}';
  }

  public static class Builder {
    private HttpRequest httpRequest;

    public Builder() {
      httpRequest = new HttpRequest();
    }

    public Builder body(String body) {
      httpRequest.body = body;
      return this;
    }

    public Builder url(String url) {
      httpRequest.url = url;
      return this;
    }

    public Builder mediaType(String mediaType) {
      httpRequest.mediaType = mediaType;
      return this;
    }

    public Builder addHeader(String name, String value) {
      httpRequest.headers.put(name, value);
      return this;
    }

    public Builder addHeaders(Map<String, String> headers) {
      if (Objects.nonNull(headers)) {
        headers.entrySet().stream()
            .forEach(
                entry -> {
                  addHeader(entry.getKey(), entry.getValue());
                });
      }
      return this;
    }

    public Builder removeHeader(String name) {
      httpRequest.headers.removeAll(name);
      return this;
    }

    public HttpRequest build() {
      return new HttpRequest(this);
    }
  }
}
