package ru.mentee.power.crm.spring;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.spring.service.LeadService;

@SpringBootApplication
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  CommandLineRunner initData(LeadService leadService) {
    return args -> {
      Address addr = new Address("Moscow", "Tverskaya", "125009");
      leadService.addLead("Ivan", "ivan@example.com", "+7-900-111-22-33", addr,
          "TechCorp", LeadStatus.NEW);
      leadService.addLead("Maria", "maria@example.com", "+7-900-222-33-44", addr,
          "DesignStudio", LeadStatus.CONTACTED);
      leadService.addLead("Alex", "alex@example.com", "+7-900-333-44-55", addr,
          "DataFlow", LeadStatus.NEW);
      leadService.addLead("Elena", "elena@example.com", "+7-900-444-55-66", addr,
          "CloudNet", LeadStatus.QUALIFIED);
      leadService.addLead("Dmitry","dmitry@example.com", "+7-900-555-66-77", addr,
          "WebSoft", LeadStatus.CONTACTED);
      leadService.addLead("Anton", "anton@example.com", "+7-900-666-77-88", addr,
          "AppleStudio", LeadStatus.LOST);
    };
  }
}
