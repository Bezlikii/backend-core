package ru.mentee.power.crm.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LeadTest {
  Lead lead;

  @BeforeEach
  void setUp() {
    lead = new Lead("L1", "test@example.com", "+71234567890", "TestCorp", "NEW");
  }

  @Test
  void shouldReturnWhenGetIdCalled() {
    String id = lead.getId();
    assertThat(id).isEqualTo("L1");
  }

  @Test
  void shouldReturnWhenGetEmailCalled() {
    String email = lead.getEmail();
    assertThat(email).isEqualTo("test@example.com");
  }

  @Test
  void shouldReturnWhenGetPhoneCalled() {
    String phone = lead.getPhone();
    assertThat(phone).isEqualTo("+71234567890");
  }

  @Test
  void shouldReturnWhenGetCompanyCalled() {
    String company = lead.getCompany();
    assertThat(company).isEqualTo("TestCorp");
  }

  @Test
  void shouldReturnWhenGetStatusCalled() {
    String status = lead.getStatus();
    assertThat(status).isEqualTo("NEW");
  }

  @Test
  void shouldReturnEWhenGetToStringCalled() {
    String toString = lead.toString();
    assertThat(toString).isEqualTo("Lead{" +
        "id='" + lead.getId() + '\'' +
        ", email='" + lead.getEmail() + '\'' +
        ", phone='" + lead.getPhone() + '\'' +
        ", company='" + lead.getCompany() + '\'' +
        ", status='" + lead.getStatus() + '\'' +
        '}');
  }
}