package de.caritas.cob.uploadservice.api.helper;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.spi.DeferredProcessingAware;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import net.logstash.logback.encoder.StreamingEncoder;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class LogstashAppender<T extends DeferredProcessingAware>
    extends AppenderBase<ILoggingEvent> {

  private int connectionTimeoutMillis = 300; // default value
  private int logBufferSize = 20; // default value
  private String logstashHost;
  private Encoder<T> encoder;
  private ArrayList<String> bufferedJsonLogRequests = Lists.newArrayListWithCapacity(100);

  @SneakyThrows
  @Override
  protected void append(ILoggingEvent event) {
    if (!isLogstashEnvVariableSet()) {
      return;
    }

    serializeEventAndAddToBuffer((T) event);
    if (isBufferSizeExceeded()) {
      sendRequestsAsynchronously();
    }
  }

  private boolean isLogstashEnvVariableSet() {
    this.logstashHost = getLogstashHost();
    if (logstashHost == null) {
      logToStandardError(
          "Logstash env variable (LOGSTASH_HOST) not set, skipping logging to logstash");
      return false;
    }
    return true;
  }

  private void serializeEventAndAddToBuffer(T event) {
    bufferedJsonLogRequests.add(serializeToJson(event));
  }

  private boolean isBufferSizeExceeded() {
    return bufferedJsonLogRequests.size() % logBufferSize == 0;
  }

  private void sendRequestsAsynchronously() {
    ArrayList<String> bufferCopy = new ArrayList<>(bufferedJsonLogRequests);
    supplyAsync(() -> sendPackageToLogstash(bufferCopy));
    bufferedJsonLogRequests.clear();
  }

  private String sendPackageToLogstash(Collection<String> logsSerializedToJson) {
    try (CloseableHttpClient client = getHttpClient()) {
      for (String log : logsSerializedToJson) {
        client.execute(prepareHttpPutRequest(log));
      }
      return "success";
    } catch (IOException e) {
      handleIOException(e);
      return "error";
    }
  }

  CloseableHttpClient getHttpClient() {
    RequestConfig config =
        RequestConfig.custom()
            .setConnectTimeout(connectionTimeoutMillis)
            .setConnectionRequestTimeout(connectionTimeoutMillis)
            .setSocketTimeout(connectionTimeoutMillis)
            .build();
    return HttpClientBuilder.create().setDefaultRequestConfig(config).build();
  }

  private void handleIOException(IOException e) {
    logToStandardError("IO Exception during http call to logstash endpoint");
    e.printStackTrace();
  }

  protected String getLogstashHost() {
    return System.getenv("LOGSTASH_HOST");
  }

  private HttpPut prepareHttpPutRequest(String json) throws UnsupportedEncodingException {
    HttpPut httpPut = new HttpPut(logstashHost);
    StringEntity entity = new StringEntity(json);
    httpPut.setEntity(entity);
    httpPut.setHeader("Content-type", "application/json");
    return httpPut;
  }

  private String serializeToJson(T event) {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    this.encode(event, outputStream);
    return outputStream.toString();
  }

  private void encode(T event, OutputStream outputStream) {
    if (this.encoder instanceof StreamingEncoder) {
      try {
        ((StreamingEncoder) this.encoder).encode(event, outputStream);
      } catch (Exception e) {
        logToStandardError("Encoder exception occurred. Logs may not be delivered to logstash");
        e.printStackTrace();
      }
    } else {
      logToStandardError("Encoder is not an instance of streaming encoder. ");
    }
  }

  private void logToStandardError(String msg) {
    System.err.println(msg);
  }

  public void setEncoder(Encoder<T> encoder) {
    this.encoder = encoder;
  }

  public void setLogBufferSize(int logBufferSize) {
    this.logBufferSize = logBufferSize;
  }

  public void setConnectionTimeoutMillis(int connectionTimeoutMillis) {
    this.connectionTimeoutMillis = connectionTimeoutMillis;
  }
}
