package ru.mentee.power.crm.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class LeadTest {
  @Test
  void shouldReturnId_whenGetIdCalled() {
    // Given
    Lead lead = new Lead("L1", "test@example.com", "+71234567890", "TestCorp", "NEW");

    // When
    String id = lead.getId();

    // Then
    assertThat(id).isEqualTo("L1");
  }

  // Допиши тесты для email, phone, company, status, toString
}