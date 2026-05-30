package ru.mentee.power.crm.web;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

class HelloCrmServerTest {

  private HelloCrmServer server;
  private HttpClient client;
  private String baseUrl;

  @BeforeEach
  void setUp() throws IOException {
    client = HttpClient.newHttpClient();
    server = new HelloCrmServer(9876);
    server.start();
    baseUrl = "http://localhost:" + 9876;
  }

  @AfterEach
  void tearDown() {
    server.stop();
  }

  @Test
  void shouldReturn200WhenHelloEndpointIsRequested() throws Exception {
    HttpResponse<String> response = client.send(
        HttpRequest.newBuilder(URI.create(baseUrl + "/hello")).GET().build(),
        HttpResponse.BodyHandlers.ofString()
    );

    assertThat(response.statusCode()).isEqualTo(200);
  }

  @Test
  void shouldReturnHtmlContentTypeWhenHelloEndpointIsRequested() throws Exception {
    HttpResponse<String> response = client.send(
        HttpRequest.newBuilder(URI.create(baseUrl + "/hello")).GET().build(),
        HttpResponse.BodyHandlers.ofString()
    );

    assertThat(response.headers().firstValue("Content-Type"))
        .hasValue("text/html; charset=UTF-8");
  }

  @Test
  void shouldReturnHelloCrmHtmlWhenHelloEndpointIsRequested() throws Exception {
    HttpResponse<String> response = client.send(
        HttpRequest.newBuilder(URI.create(baseUrl + "/hello")).GET().build(),
        HttpResponse.BodyHandlers.ofString()
    );

    assertThat(response.body()).contains("<!DOCTYPE html>");
    assertThat(response.body()).contains("<h1>Hello CRM!</h1>");
  }

  @Test
  void shouldReturn404WhenUnknownPathIsRequested() throws Exception {
    HttpResponse<String> response = client.send(
        HttpRequest.newBuilder(URI.create(baseUrl + "/unknown")).GET().build(),
        HttpResponse.BodyHandlers.ofString()
    );

    assertThat(response.statusCode()).isEqualTo(404);
  }
}
