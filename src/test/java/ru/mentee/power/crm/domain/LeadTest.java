package ru.mentee.power.crm.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Fail.fail;

class LeadTest {

  @Test
  void shouldCreateLeadWhenValidData() {
    Address address = new Address("Moscow", "Izmailovskaya", "876354");
    Contact contact = new Contact("test@gmail.com", "+76584936574", address);
    Lead lead = new Lead(UUID.randomUUID(), contact, "TestCompany", "NEW");

    assertThat(lead.contact()).isEqualTo(contact);
  }

  @Test
  void shouldAccessEmailThroughDelegationWhenLeadCreated() {
    Address address = new Address("Moscow", "Izmailovskaya", "876354");
    Contact contact = new Contact("test@gmail.com", "+76584936574", address);
    Lead lead = new Lead(UUID.randomUUID(), contact, "TestCompany", "NEW");

    assertThat(lead.contact().email()).isEqualTo("test@gmail.com");
    assertThat(lead.contact().address().city()).isEqualTo("Moscow");
  }

  @Test
  void shouldBeEqualWhenSameIdButDifferentContact() {
    UUID uuid = UUID.randomUUID();
    Address address1 = new Address("Moscow", "Izmailovskaya", "876354");
    Contact contact1 = new Contact("test1@gmail.com", "+76584936574", address1);
    Lead lead1 = new Lead(uuid, contact1, "TestCompany", "NEW");

    Address address2 = new Address("Moscow", "Izmailovskaya", "876354");
    Contact contact2 = new Contact("test2@gmail.com", "+76598762345", address2);
    Lead lead2 = new Lead(uuid, contact2, "TestCompany", "NEW");

    assertThat(lead1).isEqualTo(lead2);
  }

  @Test
  void shouldThrowExceptionWhenContactIsNull() {
    try {
      Lead lead = new Lead(UUID.randomUUID(), null, "TestCompany", "NEW");
      fail("Ожидается IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage()).contains("Contact");
    }
  }

  @Test
  void shouldThrowExceptionWhenInvalidStatus() {
    Address address = new Address("Moscow", "Izmailovskaya", "876354");
    Contact contact = new Contact("test1@gmail.com", "+76584936574", address);

    assertThatThrownBy(() -> new Lead(UUID.randomUUID(), contact, "TestCompany", "ERROR"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid");

  }

  @Test
  void shouldDemonstrateThreeLevelCompositionWhenAccessingCity() {
    Address address = new Address("Moscow", "Izmailovskaya", "876354");
    Contact contact = new Contact("test1@gmail.com", "+76584936574", address);
    Lead lead = new Lead(UUID.randomUUID(), contact, "TestCompany", "NEW");

    assertThat(lead.contact().address().city()).isEqualTo("Moscow");
  }

  @Test
  void shouldCreateLeadWhenStatusIsQualified() {
    Address address = new Address("Moscow", "Izmailovskaya", "876354");
    Contact contact = new Contact("test@gmail.com", "+76584936574", address);
    Lead lead = new Lead(UUID.randomUUID(), contact, "TestCompany", "QUALIFIED");

    assertThat(lead.status()).isEqualTo("QUALIFIED");
  }

  @Test
  void shouldCreateLeadWhenStatusIsConverted() {
    Address address = new Address("Moscow", "Izmailovskaya", "876354");
    Contact contact = new Contact("test@gmail.com", "+76584936574", address);
    Lead lead = new Lead(UUID.randomUUID(), contact, "TestCompany", "CONVERTED");

    assertThat(lead.status()).isEqualTo("CONVERTED");
  }

  @Test
  void shouldThrowExceptionWhenIdIsNull() {
    Address address = new Address("Moscow", "Izmailovskaya", "876354");
    Contact contact = new Contact("test@gmail.com", "+76584936574", address);
    assertThatThrownBy(() -> new Lead(null, contact, "TestCompany", "NEW"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Id");
  }

  @Test
  void shouldThrowExceptionWhenStatusIsNull() {
    Address address = new Address("Moscow", "Izmailovskaya", "876354");
    Contact contact = new Contact("test@gmail.com", "+76584936574", address);
    assertThatThrownBy(() -> new Lead(UUID.randomUUID(), contact, "TestCompany", null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Status");
  }
}