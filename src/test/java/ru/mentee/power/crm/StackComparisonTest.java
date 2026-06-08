package ru.mentee.power.crm;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.repository.LeadRepository;
import ru.mentee.power.crm.service.LeadService;
import ru.mentee.power.crm.servlet.LeadListServlet;
import ru.mentee.power.crm.spring.Application;

class StackComparisonTest {

  private static final int SERVLET_PORT = 8080;
  private static final int SPRING_PORT = 8081;

  private static Tomcat tomcat;
  private static ConfigurableApplicationContext springContext;
  private static long servletStartupMs;
  private static long springStartupMs;

  private HttpClient httpClient;

  @BeforeAll
  static void startBothStacks() {
    servletStartupMs = startServlet();
    springStartupMs = startSpringBoot();
  }

  @AfterAll
  static void stopBothStacks() throws Exception {
    if (tomcat != null) {
      tomcat.stop();
      tomcat.destroy();
    }
    if (springContext != null) {
      springContext.close();
    }
  }

  @BeforeEach
  void setUp() {
    httpClient = HttpClient.newHttpClient();
  }

  @Test
  @DisplayName("Оба стека должны возвращать лидов в HTML таблице")
  void shouldReturnLeadsFromBothStacks() throws Exception {
    HttpRequest servletRequest = HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:" + SERVLET_PORT + "/leads"))
        .GET()
        .build();

    HttpRequest springRequest = HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:" + SPRING_PORT + "/leads"))
        .GET()
        .build();

    HttpResponse<String> servletResponse = httpClient.send(
        servletRequest, HttpResponse.BodyHandlers.ofString());
    HttpResponse<String> springResponse = httpClient.send(
        springRequest, HttpResponse.BodyHandlers.ofString());

    assertThat(servletResponse.statusCode()).isEqualTo(200);
    assertThat(springResponse.statusCode()).isEqualTo(200);

    assertThat(servletResponse.body()).contains("<table");
    assertThat(springResponse.body()).contains("<table");
    assertThat(countTableRows(servletResponse.body()))
        .isEqualTo(countTableRows(springResponse.body()));

    int servletRows = countTableRows(servletResponse.body());
    int springRows = countTableRows(springResponse.body());

    assertThat(servletRows)
        .as("Количество лидов должно совпадать")
        .isEqualTo(springRows);

    System.out.printf("Servlet: %d лидов, Spring: %d лидов%n", servletRows, springRows);
  }

  @Test
  @DisplayName("Измерение времени старта обоих стеков")
  void shouldMeasureStartupTime() {
    System.out.println("=== Сравнение времени старта ===");
    System.out.printf("Servlet стек: %d ms%n", servletStartupMs);
    System.out.printf("Spring Boot: %d ms%n", springStartupMs);
    System.out.printf("Разница: Spring %s на %d ms%n",
        springStartupMs > servletStartupMs ? "медленнее" : "быстрее",
        Math.abs(springStartupMs - servletStartupMs));

    assertThat(servletStartupMs).isLessThan(10_000);
    assertThat(springStartupMs).isLessThan(15_000);
  }

  private int countTableRows(String html) {
    return html.split("<tr ").length - 1;
  }

  private static long startServlet() {
    LeadRepository repository = new LeadRepository();
    LeadService leadService = new LeadService(repository);

    Address addr = new Address("Moscow", "Tverskaya", "125009");
    leadService.addLead("ivan@example.com", "+7-900-111-22-33",
        addr, "TechCorp", LeadStatus.NEW);
    leadService.addLead("maria@example.com", "+7-900-222-33-44",
        addr, "DesignStudio", LeadStatus.CONTACTED);
    leadService.addLead("alex@example.com", "+7-900-333-44-55",
        addr, "DataFlow", LeadStatus.NEW);
    leadService.addLead("elena@example.com", "+7-900-444-55-66",
        addr, "CloudNet", LeadStatus.QUALIFIED);
    leadService.addLead("dmitry@example.com", "+7-900-555-66-77",
        addr, "WebSoft", LeadStatus.CONTACTED);

    Tomcat localTomcat = new Tomcat();
    localTomcat.setPort(SERVLET_PORT);
    localTomcat.setBaseDir(new File(".").getAbsolutePath());

    Context ctx = localTomcat.addContext("", new File(".").getAbsolutePath());
    ctx.getServletContext().setAttribute("leadService", leadService);
    localTomcat.addServlet(ctx, "LeadListServlet", new LeadListServlet());
    ctx.addServletMappingDecoded("/leads", "LeadListServlet");

    long start = System.nanoTime();
    try {
      localTomcat.getConnector();
      localTomcat.start();
    } catch (Exception e) {
      throw new RuntimeException("Failed to start Tomcat", e);
    }
    long elapsed = System.nanoTime() - start;

    tomcat = localTomcat;

    return elapsed / 1_000_000;
  }

  private static long startSpringBoot() {
    SpringApplication app = new SpringApplication(Application.class);
    app.setDefaultProperties(Map.of("server.port", String.valueOf(SPRING_PORT)));

    long start = System.nanoTime();
    ConfigurableApplicationContext ctx = app.run();
    long elapsed = System.nanoTime() - start;

    springContext = ctx;

    return elapsed / 1_000_000;
  }
}
