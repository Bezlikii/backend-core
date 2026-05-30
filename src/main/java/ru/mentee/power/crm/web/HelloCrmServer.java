package ru.mentee.power.crm.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class HelloCrmServer {

  private final HttpServer server;
  private final int port;

  public HelloCrmServer(int port) throws IOException {
    this.port = port;
    this.server = HttpServer.create(new InetSocketAddress(port), 0);
  }

  public void start() {
    server.createContext("/hello", new HelloHandler());
    server.start();
      System.out.println("Server started on http://localhost:" + server.getAddress().getPort());
  }

  public void stop() {
    server.stop(0);
  }

  static class HelloHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
      String htmlString = "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head><body><h1>Hello CRM!</h1></body></html>";
      byte[] bytes = htmlString.getBytes(StandardCharsets.UTF_8);
      exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
      exchange.sendResponseHeaders(200, bytes.length);
      try (OutputStream os = exchange.getResponseBody()) {
        os.write(bytes);
      }
    }
  }

}
