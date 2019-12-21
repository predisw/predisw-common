package com.predisw.common.http;

import okhttp3.Call;
import okhttp3.Dispatcher;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.backoff.BackOffExecution;
import org.springframework.util.backoff.ExponentialBackOff;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class HttpClient {

  private Logger logger = LoggerFactory.getLogger(this.getClass());

  protected OkHttpClient client;

  protected HttpClientConfig clientConfig;
  protected ExecutorService executorService;

  private static long CONNECT_TIMEOUT_MS_DEFAULT = 30000;
  private static long READ_TIMEOUT_MS_DEFAULT = 30000;
  private static long WRITE_TIMEOUT_MS_DEFAULT = 30000;
  private static int MAX_REQS_PER_HOST_DEFAULT = 300;
  private static int MAX_REQS_DEFAULT = 300;
  private static int CORE_THREADS_DEFAULT = 1;
  private static int MAX_THREADS_DEFAULT = 100;
  private static String HTTP_THREAD_NAME_DEFAULT = "httpClient";

  public HttpClient() {
    this(null, null);
  }

  public HttpClient(HttpClientConfig httpClientConfig) {
    this(httpClientConfig, null);
  }

  public HttpClient(HttpClientConfig httpClientConfig, ExecutorService executorService) {
    this.clientConfig = httpClientConfig;
    this.executorService = executorService;
    init();
  }

  private void init() {
    if (this.clientConfig == null) {
      this.clientConfig = httpClientConfig();
    }

    if (this.executorService == null) {
      this.executorService =
          new ThreadPoolExecutor(
              CORE_THREADS_DEFAULT,
              MAX_THREADS_DEFAULT,
              60L,
              TimeUnit.SECONDS,
              new SynchronousQueue(),
              Util.threadFactory(HTTP_THREAD_NAME_DEFAULT, false));
    }

    OkHttpClient.Builder clientBuilder =
        new OkHttpClient.Builder()
            .dispatcher(dispatcher(this.clientConfig, this.executorService))
            .connectTimeout(this.clientConfig.getConnectTimeoutMs(), TimeUnit.MILLISECONDS)
            .writeTimeout(this.clientConfig.getWriteTimeoutMs(), TimeUnit.MILLISECONDS)
            .readTimeout(this.clientConfig.getReadTimeoutMs(), TimeUnit.MILLISECONDS);

    if (clientConfig.getProtocols() != null) {
      clientBuilder.protocols(clientConfig.getProtocols());
    }

    if (clientConfig.isProxyEnable()) {
      Proxy proxy =
          new Proxy(
              Proxy.Type.HTTP,
              new InetSocketAddress(clientConfig.getProxyHost(), clientConfig.getProxyPort()));
      clientBuilder.proxy(proxy);
    }
    client = clientBuilder.build();
  }

  public void writeHttpResponse(HttpServletResponse response, String httpBody) throws IOException {
    java.io.PrintWriter writer = null;
    try {
      writer = response.getWriter();
      writer.write(httpBody);
    } finally {
      if (writer != null) {
        writer.flush();
      }
    }
  }

  /**
   * would ignore the httpRequest body with Method GET
   *
   * @param httpRequest
   * @return
   */
  public CompletableFuture<HttpResponse> getAsync(HttpRequest httpRequest) {
    logger.debug("send the request with GET method AND the request is {}", httpRequest);
    return sendHttpRequestAsync(() -> buildGetRequest(httpRequest));
  }

  /**
   * would ignore the httpRequest body with Method DELETE
   *
   * @param httpRequest
   * @return
   */
  public CompletableFuture<HttpResponse> deleteAsync(HttpRequest httpRequest) {
    logger.debug("send the request with DELETE method AND the request is {}", httpRequest);
    return sendHttpRequestAsync(() -> buildDeleteRequest(httpRequest));
  }

  public CompletableFuture<HttpResponse> putAsync(HttpRequest httpRequest) {
    logger.debug("send the request with PUT method AND the request is {}", httpRequest);
    return sendHttpRequestAsync(() -> buildPutRequest(httpRequest));
  }

  public CompletableFuture<HttpResponse> postAsync(HttpRequest httpRequest) {
    logger.debug("send the request with POST method AND the request is {}", httpRequest);
    return sendHttpRequestAsync(() -> buildPostRequest(httpRequest));
  }

  private CompletableFuture<HttpResponse> sendHttpRequestAsync(Supplier<Request> httpRequest) {
    Request request = httpRequest.get();
    BackOffExecution backOffExecution = this.getExponentBackoffExecution();
    Call call = client.newCall(request);
    CompletableFuture<Response> completedFuture = new CompletableFuture<Response>() {};
    okHttp3Async(call, httpRequest, backOffExecution, completedFuture);
    CompletableFuture<HttpResponse> httpRes = handleAsyncResponse(completedFuture);
    return httpRes;
  }

  private CompletableFuture<HttpResponse> handleAsyncResponse(
      CompletableFuture<Response> response) {
    CompletableFuture<HttpResponse> httpRes =
        response.handle(
            (res, ex) -> {
              if (ex != null) {
                throwException(ex);
              }
              try (ResponseBody responseBody = res.body()) {
                HttpResponse.Builder builder = new HttpResponse.Builder();
                builder
                    .statusCode(res.code())
                    .body(responseBody.string())
                    .headers(res.headers())
                    .protocol(res.protocol());
                return builder.build();
              } catch (Exception e) {
                throwException(e);
              }
              return null;
            });

    return httpRes;
  }

  private Request buildPostRequest(HttpRequest httpRequest) {
    MediaType mediaType = MediaType.parse(httpRequest.getMediaType());
    Request.Builder reqBuilder =
        buildCommonRequestBuilder(httpRequest)
            .post(RequestBody.create(mediaType, httpRequest.getBody()));

    Request request = reqBuilder.build();
    return request;
  }

  private Request buildPutRequest(HttpRequest httpRequest) {
    MediaType mediaType = MediaType.parse(httpRequest.getMediaType());
    Request.Builder reqBuilder =
        buildCommonRequestBuilder(httpRequest)
            .put(RequestBody.create(mediaType, httpRequest.getBody()));

    Request request = reqBuilder.build();
    return request;
  }

  private Request buildDeleteRequest(HttpRequest httpRequest) {
    Request.Builder reqBuilder = buildCommonRequestBuilder(httpRequest).delete();

    Request request = reqBuilder.build();
    return request;
  }

  private Request buildGetRequest(HttpRequest httpRequest) {
    Request.Builder reqBuilder = buildCommonRequestBuilder(httpRequest).get();

    Request request = reqBuilder.build();
    return request;
  }

  private Request.Builder buildCommonRequestBuilder(HttpRequest httpRequest) {
    HttpUrl httpUrl = HttpUrl.parse(httpRequest.getUrl());

    Headers headers = new Headers.Builder().build();
    for (Map.Entry<String, String> header : httpRequest.getHeaders().entries()) {
      headers = headers.newBuilder().add(header.getKey(), header.getValue()).build();
    }

    Request.Builder reqBuilder = new Request.Builder().url(httpUrl).headers(headers);

    return reqBuilder;
  }

  private void okHttp3Async(
      Call call,
      Supplier<Request> httpRequest,
      BackOffExecution backOffExecution,
      CompletableFuture<Response> completedFuture) {

    okhttp3.Callback callback =
        new okhttp3.Callback() {
          @Override
          public void onFailure(Call request, IOException e) {
            logger.error("HttpClient request exception", e);
            if (e instanceof ConnectException) {
              logger.error("ready to retry");
              exponentBackoffRetry(httpRequest, backOffExecution, completedFuture, e);
            } else {
              completedFuture.completeExceptionally(e);
            }
          }

          @Override
          public void onResponse(Call request, Response response) {
            completedFuture.complete(response);
          }
        };

    call.enqueue(callback);
  }

  private void throwException(Throwable th) {
    throwException(500, th);
  }

  private void throwException(int statusCode, Throwable th) {
    throw new HttpResponseException(statusCode, "Unexpected code " + statusCode, th);
  }

  private Dispatcher dispatcher(
      HttpClientConfig httpClientConfig, ExecutorService executorService) {
    Dispatcher dispatcher = new Dispatcher(executorService);
    dispatcher.setMaxRequestsPerHost(httpClientConfig.getMaxRequestsPerHost());
    dispatcher.setMaxRequests(httpClientConfig.getMaxRequests());
    return dispatcher;
  }

  private HttpClientConfig httpClientConfig() {
    HttpClientConfig clientConfig =
        new HttpClientConfig.Builder()
            .connectTimeout(CONNECT_TIMEOUT_MS_DEFAULT)
            .readTimeout(READ_TIMEOUT_MS_DEFAULT)
            .writeTimeout(WRITE_TIMEOUT_MS_DEFAULT)
            .maxRequestsPerHost(MAX_REQS_PER_HOST_DEFAULT)
            .maxRequests(MAX_REQS_DEFAULT)
            .build();

    return clientConfig;
  }

  public OkHttpClient getClient() {
    return client;
  }

  public void setClient(OkHttpClient client) {
    this.client = client;
  }

  private void exponentBackoffRetry(
      Supplier<Request> httpRequest,
      BackOffExecution backOffExecution,
      CompletableFuture<Response> completedFuture,
      IOException e) {

    long timeWait = backOffExecution.nextBackOff();
    if (timeWait != -1L) {
      try {
        Thread.sleep(timeWait);
      } catch (InterruptedException var8) {
        logger.error("Exponent backoff retry failure.", var8);
      }
      Request request = httpRequest.get();
      Call call = client.newCall(request);
      okHttp3Async(call, httpRequest, backOffExecution, completedFuture);
    } else {
      completedFuture.completeExceptionally(e);
      return;
    }
  }

  protected BackOffExecution getExponentBackoffExecution() {
    ExponentialBackOff exponentialBackOff = new ExponentialBackOff(1000L, 2.0D);
    exponentialBackOff.setMaxElapsedTime(2000L);
    return exponentialBackOff.start();
  }
}
