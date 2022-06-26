package io.vividcode.loomfaq.app;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;

/**
 * A simple HTTP server using virtual threads.
 * <p>
 * Send requests to {@code http://localhost:8000/time} to view current time and
 * virtual thread name that executes the current request
 */
public class SimpleHttpServer {

  public static void main(String[] args) throws IOException {
    new SimpleHttpServer().start();
  }

  public void start() throws IOException {
    var server = HttpServer.create(new InetSocketAddress(8000), 0);
    server.createContext("/time", new TimeHandler());
    server.setExecutor(Executors.newThreadPerTaskExecutor(Thread.ofVirtual()
        .name("time-server-", 1)
        .factory()));
    server.start();
    System.out.println("Time server started");
  }

  private static class TimeHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
      var response = String.format("%s, reported on %s",
          LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
          Thread.currentThread().getName());
      exchange.sendResponseHeaders(200, response.length());
      try (var out = exchange.getResponseBody()) {
        out.write(response.getBytes());
      }
    }
  }
}
