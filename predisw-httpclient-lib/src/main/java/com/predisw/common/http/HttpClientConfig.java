package com.predisw.common.http;

import com.google.common.collect.ImmutableList;
import okhttp3.Protocol;

import java.util.List;
import java.util.Objects;

public class HttpClientConfig {

  private long connectTimeoutMs;
  private long writeTimeoutMs;
  private long readTimeoutMs;

  private int maxRequestsPerHost;
  private int maxRequests;

  private List<Protocol> protocols;
  private boolean proxyEnable;
  private String proxyHost;
  private int proxyPort;

  private Builder builder;

  public Builder builder() {
    return this.builder;
  }

  private HttpClientConfig() {}

  private HttpClientConfig(Builder builder) {
    this.connectTimeoutMs = builder.config.connectTimeoutMs;
    this.writeTimeoutMs = builder.config.writeTimeoutMs;
    this.readTimeoutMs = builder.config.readTimeoutMs;
    this.maxRequestsPerHost = builder.config.maxRequestsPerHost;
    this.maxRequests = builder.config.maxRequests;
    this.protocols = builder.config.protocols;
    this.proxyEnable = builder.config.proxyEnable;
    this.proxyHost = builder.config.proxyHost;
    this.proxyPort = builder.config.proxyPort;
    this.builder = builder;
  }

  public boolean isProxyEnable() {
        return proxyEnable;
    }

  public String getProxyHost() {
        return proxyHost;
    }

  public int getProxyPort() {
        return proxyPort;
    }

  public long getConnectTimeoutMs() {
    return connectTimeoutMs;
  }

  public long getWriteTimeoutMs() {
    return writeTimeoutMs;
  }

  public long getReadTimeoutMs() {
    return readTimeoutMs;
  }

  public int getMaxRequestsPerHost() {
    return maxRequestsPerHost;
  }

  public int getMaxRequests() {
    return maxRequests;
  }

  public List<Protocol> getProtocols() {
    if (Objects.nonNull(protocols)) {
      return ImmutableList.copyOf(protocols);
    }
    return null;
  }

  public static class Builder {
    private HttpClientConfig config;

    public Builder() {
      config = new HttpClientConfig();
    }

    public Builder connectTimeout(long connectTimeout) {
      config.connectTimeoutMs = connectTimeout;
      return this;
    }

    public Builder writeTimeout(long writeTimeout) {
      config.writeTimeoutMs = writeTimeout;
      return this;
    }

    public Builder readTimeout(long readTimeout) {
      config.readTimeoutMs = readTimeout;
      return this;
    }

    public Builder maxRequestsPerHost(int maxRequestsPerHost) {
      config.maxRequestsPerHost = maxRequestsPerHost;
      return this;
    }

    public Builder maxRequests(int maxRequests) {
      config.maxRequests = maxRequests;
      return this;
    }

    public Builder protocols(List<Protocol> protocols) {
      config.protocols = protocols;
      return this;
    }

    public Builder proxyEnable(boolean proxyEnable){
      config.proxyEnable = proxyEnable;
      return this;
    }

    public Builder proxyHost(String proxyHost){
      config.proxyHost = proxyHost;
      return this;
    }

    public Builder proxyPort(int proxyPort){
      config.proxyPort = proxyPort;
      return this;
    }

    public HttpClientConfig build() {
      return new HttpClientConfig(this);
    }
  }
}
