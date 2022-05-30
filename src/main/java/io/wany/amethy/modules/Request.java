package io.wany.amethy.modules;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.wany.amethy.Console;
import io.wany.amethy.modules.Request.Options.Method;
import io.wany.amethy.modules.Request.Options.ResponseType;

public class Request extends EventEmitter {

  public static String USER_AGENT = "Amethy";

  private final URL url;
  private final Options options;
  private final String body;

  private HttpURLConnection req;
  private CompletableFuture<Object> future;

  public Request(URL url, Options options, String body) {
    super();

    this.url = url;
    this.options = options;
    this.body = body;

    if (!this.options.HEADERS.containsKey("User-Agent")) {
      this.options.HEADERS.replace("User-Agent", USER_AGENT);
    }
  }

  public CompletableFuture<Object> future() throws IOException {
    future = new CompletableFuture<Object>();
    return future;
  }

  public void send() throws IOException {
    this.req = (HttpURLConnection) this.url.openConnection();
    this.req.setRequestMethod(this.options.METHOD.toString());
    this.options.HEADERS.forEach((key, value) -> {
      if (value != null) {
        this.req.setRequestProperty(key, value);
      }
    });
    this.req.setConnectTimeout(this.options.TIMEOUT);

    if (List.of(Method.POST, Method.PUT, Method.PATCH, Method.DELETE).contains(this.options.METHOD)
        && this.body != null && !this.body.isBlank()) {
      this.req.setDoOutput(true);
      DataOutputStream outputStream = new DataOutputStream(this.req.getOutputStream());
      outputStream.writeBytes(this.body);
      outputStream.flush();
      outputStream.close();
    }

    Object response;
    switch (this.options.RESPONSETYPE) {
      case JSON: {
        response = this.JSONResponse();
        break;
      }
      case STREAM: {
        response = this.streamResponse();
        break;
      }
      case STRING:
      default: {
        response = this.stringResponse();
        break;
      }
    }

    future.completeAsync(() -> response);

    this.emit("response", response);
    this.emit("res", response);
    this.emit("r", response);

    int status = this.req.getResponseCode();
    this.emit(String.valueOf(status), response);
    if (100 <= status && status < 200) {
      this.emit("info", response);
      this.emit("i", response);
    } else if (200 <= status && status < 300) {
      this.emit("success", response);
      this.emit("ok", response);
      this.emit("s", response);
      this.emit("o", response);
    } else if (300 <= status && status < 400) {
      this.emit("redirect", response);
      this.emit("redir", response);
      this.emit("d", response);
    } else if (400 <= status && status < 500) {
      this.emit("error", response);
      this.emit("err", response);
      this.emit("e", response);
    } else if (500 <= status && status < 600) {
      this.emit("error", response);
      this.emit("err", response);
      this.emit("e", response);
    } else {
      this.emit("what", response);
    }
  }

  private String stringResponse() throws IOException {
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.req.getInputStream()));
    StringBuffer stringBuffer = new StringBuffer();
    String inputLine;

    while ((inputLine = bufferedReader.readLine()) != null) {
      stringBuffer.append(inputLine);
    }
    bufferedReader.close();

    String response = stringBuffer.toString();
    return response;
  }

  private JsonObject JSONResponse() throws IOException {
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.req.getInputStream()));
    StringBuffer stringBuffer = new StringBuffer();
    String inputLine;

    while ((inputLine = bufferedReader.readLine()) != null) {
      stringBuffer.append(inputLine);
    }
    bufferedReader.close();

    String responseString = stringBuffer.toString();
    JsonElement responseElement = JsonParser.parseString(responseString);
    JsonObject responseObject = responseElement.getAsJsonObject();
    return responseObject;
  }

  private InputStreamReader streamResponse() throws IOException {
    return new InputStreamReader(this.req.getInputStream());
  }

  public static class Options {
    public Method METHOD;
    public ResponseType RESPONSETYPE;
    public HashMap<String, String> HEADERS;
    public int TIMEOUT;

    public Options() {
      this.METHOD = Method.GET;
      this.HEADERS = new HashMap<>();
      this.TIMEOUT = 2000;
      this.RESPONSETYPE = ResponseType.STRING;
    }

    public Options(Method method, ResponseType responseType) {
      this.METHOD = method;
      this.HEADERS = new HashMap<>();
      this.TIMEOUT = 2000;
      this.RESPONSETYPE = responseType;
    }

    public enum Method {
      HEAD,
      OPTIONS,
      GET,
      POST,
      PUT,
      PATCH,
      DELETE
    }

    public enum ResponseType {
      STRING,
      JSON,
      STREAM
    }
  }

  public static void consumerRequest(URL url, Options options, String body, Consumer<Object[]> callback)
      throws IOException {
    Request req = new Request(url, options, body);
    req.on("response", (res) -> {
      callback.accept(res);
    });
    req.send();
  }

  public static CompletableFuture<Object> futureRequest(URL url, Options options, String body) throws IOException {
    Request req = new Request(url, options, body);
    CompletableFuture<Object> future = req.future();
    req.send();
    return future;
  }

  public static Object syncRequest(URL url, Options options, String body)
      throws MalformedURLException, InterruptedException, ExecutionException, IOException {
    return futureRequest(url, options, body).get();
  }

  public static String syncStringRequest(Method method, String url, String body, String auth, String ua)
      throws MalformedURLException, InterruptedException, ExecutionException, IOException {
    Options options = new Options(method, ResponseType.STRING);
    options.HEADERS.put("User-Agent", ua);
    options.HEADERS.put("Authorization", auth);
    return (String) syncRequest(new URL(url), options, body);
  }

  public static JsonObject syncJSONRequest(Method method, String url, String body, String auth, String ua)
      throws MalformedURLException, InterruptedException, ExecutionException, IOException {
    Options options = new Options(method, ResponseType.JSON);
    options.HEADERS.put("User-Agent", ua);
    options.HEADERS.put("Authorization", auth);
    options.HEADERS.put("Content-Type", "application/json");
    return (JsonObject) syncRequest(new URL(url), options, body);
  }

  public static InputStreamReader syncStreamRequest(Method method, String url, String body, String auth, String ua)
      throws MalformedURLException, InterruptedException, ExecutionException, IOException {
    Options options = new Options(method, ResponseType.STREAM);
    options.HEADERS.put("User-Agent", ua);
    options.HEADERS.put("Authorization", auth);
    return (InputStreamReader) syncRequest(new URL(url), options, body);
  }

  public static String get(String url)
      throws MalformedURLException, InterruptedException, ExecutionException, IOException {
    return syncStringRequest(Method.GET, url, null, null, null);
  }

  public static String post(String url, String body)
      throws MalformedURLException, InterruptedException, ExecutionException, IOException {
    return syncStringRequest(Method.POST, url, body, null, null);
  }

  public static JsonObject JSONGet(String url)
      throws MalformedURLException, InterruptedException, ExecutionException, IOException {
    return syncJSONRequest(Method.GET, url, null, null, null);
  }

  public static JsonObject JSONGet(String url, String auth)
      throws MalformedURLException, InterruptedException, ExecutionException, IOException {
    return syncJSONRequest(Method.GET, url, null, auth, null);
  }

  public static JsonObject JSONPost(String url, String body)
      throws MalformedURLException, InterruptedException, ExecutionException, IOException {
    return syncJSONRequest(Method.POST, url, body, null, null);
  }

  public static JsonObject JSONPost(String url, JsonObject body)
      throws MalformedURLException, InterruptedException, ExecutionException, IOException {
    return syncJSONRequest(Method.POST, url, body.toString(), null, null);
  }

  public static JsonObject JSONPost(String url, JsonObject body, String auth)
      throws MalformedURLException, InterruptedException, ExecutionException, IOException {
    return syncJSONRequest(Method.POST, url, body.toString(), auth, null);
  }

}
