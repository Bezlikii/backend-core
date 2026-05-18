package ru.mentee.power.crm.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class LeadTest {
  Lead lead;

  @BeforeEach
  void setUp() {
    lead = new Lead(UUID.randomUUID(), "test@example.com", "+71234567890", "TestCorp", "NEW");
  }

  @Test
  void shouldReturnWhenGetIdCalled() {
    UUID id = lead.getId();
    assertThat(id).isNotNull();
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

  @Test
  void shouldCreateLeadWhenValidData() {
    UUID id = UUID.randomUUID();
    Lead lead1 = new Lead(id, "test@gmail.com", "+7000000000", "Company", "NEW");
    assertThat(lead1.getId()).isEqualTo(id);

  }

  @Test
  void shouldGenerateUniqueIdsWhenMultipleLeads() {
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();
    Lead lead1 = new Lead(id1, "test@gmail.com", "+7000000000", "Company", "NEW");
    Lead lead2 = new Lead(id2, "othertest@gmail.com", "+8000000000", "Company2", "NEW");
    assertThat(lead1.getId()).isNotEqualTo(lead2.getId());
  }
}