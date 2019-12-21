package com.predisw.common.http;

import static org.junit.Assert.fail;

import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.Runner;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import okhttp3.internal.http2.ConnectionShutdownException;
import okhttp3.internal.http2.StreamResetException;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HttpClient.class, Request.class})
@PowerMockIgnore({"javax.management.*", "javax.net.*", "javax.crypto.*", "javax.security.*"})
public class HttpClientTest {

  private HttpClient httpClientVh2c;
  private HttpClient httpClientV1_1;
  private HttpClientConfig clientConfig;
  private HttpRequest httpRequestLocalV1_1;
  private static HttpServer httpServerV1_1;
  private static int httpServerV1_1Port = 12307;
  private static String httpResponseBody = "{\"output\":\"Hello World.\"}";
  private ExecutorService executorService;
  private int maxThread = 10;

  private Response httpRespMock;
  private CompletableFuture<Response> responseFuture;

  private String resHeaderKey = "Server";
  private String resHeaderValue = "MockHttpServer";

  @BeforeClass
  public static void init_beforeClass() {
    httpServerV1_1 = Moco.httpServer(httpServerV1_1Port);
    httpServerV1_1.response(httpResponseBody);
    Runner.runner(httpServerV1_1).start();
  }

  @Before
  public void init() {
    executorService =
        new ThreadPoolExecutor(
            5,
            maxThread,
            60L,
            TimeUnit.SECONDS,
            new SynchronousQueue(),
            Util.threadFactory("OkHttp Dispatcher", false));
    clientConfig =
        new HttpClientConfig.Builder()
            .connectTimeout(10000)
            .readTimeout(30000)
            .writeTimeout(10000)
            .maxRequestsPerHost(10)
            .maxRequests(100)
            .build();

    // Protocol.H2_PRIOR_KNOWLEDGE  is equal to h2c(http2.0 clearText, no TLS)
    // Protocol.HTTP_2 need TLS

    httpClientV1_1 =
        new HttpClient(
            clientConfig.builder().protocols(Arrays.asList(Protocol.HTTP_1_1)).build(),
            executorService);
    httpClientVh2c =
        new HttpClient(
            clientConfig.builder().protocols(Arrays.asList(Protocol.H2_PRIOR_KNOWLEDGE)).build(),
            executorService);

    httpRequestLocalV1_1 =
        new HttpRequest.Builder()
            .url("http://localhost:" + httpServerV1_1Port)
            .body("{\"a\":\"b\"}")
            .build();

    responseFuture = new CompletableFuture<>();

    httpRespMock =
        new Response.Builder()
            .request(PowerMockito.mock(Request.class))
            .protocol(Protocol.HTTP_1_1)
            .message(httpResponseBody)
            .header(resHeaderKey, resHeaderValue)
            .code(200)
            .body(
                ResponseBody.create(
                    MediaType.parse("application/json; charset=utf-8"), httpResponseBody))
            .build();
  }

  @Test
  public void httpClient_default() {
    httpClientV1_1 = new HttpClient();
    HttpResponse httpResponse = httpClientV1_1.postAsync(httpRequestLocalV1_1).join();

    System.out.println(httpResponse);

    Assert.assertEquals(200, httpResponse.getStatusCode());
  }

  @Test
  public void postAsyncHttp1_1_ResponseOk() {
    HttpResponse httpResponse = httpClientV1_1.postAsync(httpRequestLocalV1_1).join();

    System.out.println(httpResponse);

    Assert.assertEquals(200, httpResponse.getStatusCode());
    Assert.assertEquals(httpResponseBody, httpResponse.getBody());
    Assert.assertEquals(
        String.valueOf(httpResponseBody.length()), httpResponse.getHeaders().get("Content-Length"));
    Assert.assertEquals(Protocol.HTTP_1_1, httpResponse.getProtocol());
  }

  @Test
  public void getAsyncHttp1_1_ResponseOk() {
    HttpResponse httpResponse = httpClientV1_1.getAsync(httpRequestLocalV1_1).join();

    System.out.println(httpResponse);

    Assert.assertEquals(200, httpResponse.getStatusCode());
    Assert.assertEquals(httpResponseBody, httpResponse.getBody());
    Assert.assertEquals(
        String.valueOf(httpResponseBody.length()), httpResponse.getHeaders().get("Content-Length"));
    Assert.assertEquals(Protocol.HTTP_1_1, httpResponse.getProtocol());
  }

  @Test
  public void putAsyncHttp1_1_ResponseOk() {
    HttpResponse httpResponse = httpClientV1_1.putAsync(httpRequestLocalV1_1).join();

    System.out.println(httpResponse);

    Assert.assertEquals(200, httpResponse.getStatusCode());
    Assert.assertEquals(httpResponseBody, httpResponse.getBody());
    Assert.assertEquals(
        String.valueOf(httpResponseBody.length()), httpResponse.getHeaders().get("Content-Length"));
    Assert.assertEquals(Protocol.HTTP_1_1, httpResponse.getProtocol());
  }

  @Test
  public void deleteAsyncHttp1_1_ResponseOk() {
    HttpResponse httpResponse = httpClientV1_1.deleteAsync(httpRequestLocalV1_1).join();

    System.out.println(httpResponse);

    Assert.assertEquals(200, httpResponse.getStatusCode());
    Assert.assertEquals(httpResponseBody, httpResponse.getBody());
    Assert.assertEquals(
        String.valueOf(httpResponseBody.length()), httpResponse.getHeaders().get("Content-Length"));
    Assert.assertEquals(Protocol.HTTP_1_1, httpResponse.getProtocol());
  }

  //    @Test
  public void postAsyncHttp1_1_WithMockResponseOK() throws Exception {

    httpRequestLocalV1_1 =
        httpRequestLocalV1_1.builder().addHeader("Accept", "application/json").build();
    responseFuture.complete(httpRespMock);

    httpClientV1_1 = mockedOkHttp3AsyncMethodHttpClient(httpClientV1_1, responseFuture);
    HttpResponse httpResponse = httpClientV1_1.postAsync(httpRequestLocalV1_1).join();

    System.out.println(httpResponse);

    Assert.assertEquals(200, httpResponse.getStatusCode());
    Assert.assertEquals(httpResponseBody, httpResponse.getBody());
    Assert.assertEquals(httpResponse.getHeaders().get(resHeaderKey), resHeaderValue);
  }

  @Test
  public void postAsyncIfAsync() {
    Boolean isAsync = true;
    Map<String, Boolean> container = new HashMap<>(1);
    container.put("isAsync", isAsync);
    CompletableFuture<HttpResponse> httpResponseFuture =
        httpClientV1_1
            .postAsync(httpRequestLocalV1_1)
            .thenApply(
                res -> {
                  System.out.println(
                      "output in thenApply before sleep with thread: "
                          + Thread.currentThread().getName());
                  System.out.println("result " + res);
                  try {
                    Thread.sleep(1000);
                  } catch (InterruptedException e) {
                    e.printStackTrace();
                  }
                  System.out.println(
                      "output in thenApply after sleep with thread: "
                          + Thread.currentThread().getName());
                  container.put("isAsync", false);
                  return res;
                });
    System.out.println("should output fist in main thread: " + Thread.currentThread().getName());
    Assert.assertEquals(true, container.get("isAsync"));
    HttpResponse httpResponse = httpResponseFuture.join();
    Assert.assertEquals(200, httpResponse.getStatusCode());
  }

  @Test
  public void postAsyncProtocolH2c_To_Http1_1Server() {

    try {
      httpClientVh2c.postAsync(httpRequestLocalV1_1).join();
      fail("should not reach here.");
    } catch (Exception ex) {
      System.out.println("-------------Client Exception -------------");
      ex.printStackTrace();
      Assertions.assertThat(ex).isInstanceOf(CompletionException.class);
      Assertions.assertThat(ex.getCause()).isInstanceOf(HttpResponseException.class);
      Assertions.assertThat(((HttpResponseException) ex.getCause()).getStatusCode()).isEqualTo(500);

      Assertions.assertThat(
              ex.getCause().getCause() instanceof StreamResetException
                  || ex.getCause().getCause() instanceof ConnectionShutdownException)
          .isTrue();
      if (ex.getCause().getCause() instanceof StreamResetException) {
        Assertions.assertThat(ex.getCause().getCause()).hasMessageContaining("PROTOCOL_ERROR");
      }
    }
  }

  //    @Test
  public void postAsync_Exception_CallBackException() throws Exception {
    String exMsg = "Can not connect AS";
    responseFuture.completeExceptionally(new IOException(exMsg));
    httpClientV1_1 = mockedOkHttp3AsyncMethodHttpClient(httpClientV1_1, responseFuture);
    try {
      httpClientV1_1.postAsync(httpRequestLocalV1_1).join();
      fail("should not reach here");
    } catch (CompletionException ex) {
      ex.printStackTrace();
      Assertions.assertThat(ex).hasCauseInstanceOf(HttpResponseException.class);
      Assertions.assertThat(ex.getCause()).hasCauseInstanceOf(IOException.class);
      Assertions.assertThat(((HttpResponseException) ex.getCause()).getStatusCode()).isEqualTo(500);
      Assertions.assertThat(ex.getCause().getCause()).hasMessage(exMsg);
    }
  }

  //    @Test
  public void postAsync_Exception_ResponseNull() throws Exception {
    responseFuture.complete(null);
    httpClientV1_1 = mockedOkHttp3AsyncMethodHttpClient(httpClientV1_1, responseFuture);
    try {
      httpClientV1_1.postAsync(httpRequestLocalV1_1).join();
      fail("should not reach here");
    } catch (CompletionException ex) {
      ex.printStackTrace();
      Assertions.assertThat(ex).hasCauseInstanceOf(HttpResponseException.class);
      Assertions.assertThat(ex.getCause()).hasCauseInstanceOf(NullPointerException.class);
      Assertions.assertThat(((HttpResponseException) ex.getCause()).getStatusCode()).isEqualTo(500);
    }
  }

  private HttpClient mockedOkHttp3AsyncMethodHttpClient(
      HttpClient httpClient, CompletableFuture<Response> responseFuture) throws Exception {
    HttpClient httpClientSpy = PowerMockito.spy(httpClient);
    PowerMockito.doReturn(responseFuture)
        .when(httpClientSpy, PowerMockito.method(HttpClient.class, "okHttp3Async", Call.class))
        .withArguments(Mockito.any(Call.class));
    return httpClientSpy;
  }
}
