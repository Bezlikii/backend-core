package ru.mentee.power.crm.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class LeadTest {

  @Test
  void shouldCreateLeadWhenValidData() {
    Address address = new Address("Moscow", "Izmailovskaya", "876354");
    Contact contact = new Contact("test@gmail.com", "+76584936574", address);
    Lead lead = new Lead(UUID.randomUUID(), contact, "TestCompany", LeadStatus.NEW, LeadIndustry.IT);

    assertThat(lead.contact()).isEqualTo(contact);
  }

  @Test
  void shouldAccessEmailThroughDelegationWhenLeadCreated() {
    Address address = new Address("Moscow", "Izmailovskaya", "876354");
    Contact contact = new Contact("test@gmail.com", "+76584936574", address);
    Lead lead = new Lead(UUID.randomUUID(), contact, "TestCompany", LeadStatus.NEW, LeadIndustry.IT);

    assertThat(lead.contact().email()).isEqualTo("test@gmail.com");
    assertThat(lead.contact().address().city()).isEqualTo("Moscow");
  }

  @Test
  void shouldBeEqualWhenSameIdButDifferentContact() {
    UUID uuid = UUID.randomUUID();
    Address address1 = new Address("Moscow", "Izmailovskaya", "876354");
    Contact contact1 = new Contact("test1@gmail.com", "+76584936574", address1);
    Lead lead1 = new Lead(uuid, contact1, "TestCompany", LeadStatus.NEW, LeadIndustry.IT);

    Address address2 = new Address("Moscow", "Izmailovskaya", "876354");
    Contact contact2 = new Contact("test2@gmail.com", "+76598762345", address2);
    Lead lead2 = new Lead(uuid, contact2, "TestCompany", LeadStatus.NEW, LeadIndustry.IT);

    assertThat(lead1).isEqualTo(lead2);
  }

  @Test
  void shouldThrowExceptionWhenContactIsNull() {
    assertThatThrownBy(() -> new Lead(UUID.randomUUID(), null, "TestCompany", LeadStatus.NEW, LeadIndustry.IT))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Contact");
  }

  @Test
  void shouldThrowExceptionWhenStatusIsNull() {
    Address address = new Address("Moscow", "Izmailovskaya", "876354");
    Contact contact = new Contact("test1@gmail.com", "+76584936574", address);

    assertThatThrownBy(() -> new Lead(UUID.randomUUID(), contact, "TestCompany", null, LeadIndustry.IT))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Status");
  }

  @Test
  void shouldThrowExceptionWhenIndustryIsNull() {
    Address address = new Address("Moscow", "Izmailovskaya", "876354");
    Contact contact = new Contact("test@gmail.com", "+76584936574", address);

    assertThatThrownBy(() -> new Lead(UUID.randomUUID(), contact, "TestCompany", LeadStatus.NEW, null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Industry");
  }

  @Test
  void shouldDemonstrateThreeLevelCompositionWhenAccessingCity() {
    Address address = new Address("Moscow", "Izmailovskaya", "876354");
    Contact contact = new Contact("test1@gmail.com", "+76584936574", address);
    Lead lead = new Lead(UUID.randomUUID(), contact, "TestCompany", LeadStatus.NEW, LeadIndustry.IT);

    assertThat(lead.contact().address().city()).isEqualTo("Moscow");
  }

  @Test
  void shouldCreateLeadWhenStatusIsNew() {
    Address address = new Address("Moscow", "Izmailovskaya", "876354");
    Contact contact = new Contact("test@gmail.com", "+76584936574", address);
    Lead lead = new Lead(UUID.randomUUID(), contact, "TestCompany", LeadStatus.NEW, LeadIndustry.IT);

    assertThat(lead.status()).isEqualTo(LeadStatus.NEW);
  }

  @Test
  void shouldCreateLeadWhenStatusIsContacted() {
    Address address = new Address("Moscow", "Izmailovskaya", "876354");
    Contact contact = new Contact("test@gmail.com", "+76584936574", address);
    Lead lead = new Lead(UUID.randomUUID(), contact, "TestCompany", LeadStatus.CONTACTED, LeadIndustry.IT);

    assertThat(lead.status()).isEqualTo(LeadStatus.CONTACTED);
  }

  @Test
  void shouldCreateLeadWhenStatusIsQualified() {
    Address address = new Address("Moscow", "Izmailovskaya", "876354");
    Contact contact = new Contact("test@gmail.com", "+76584936574", address);
    Lead lead = new Lead(UUID.randomUUID(), contact, "TestCompany", LeadStatus.QUALIFIED, LeadIndustry.IT);

    assertThat(lead.status()).isEqualTo(LeadStatus.QUALIFIED);
  }

  @Test
  void shouldThrowExceptionWhenIdIsNull() {
    Address address = new Address("Moscow", "Izmailovskaya", "876354");
    Contact contact = new Contact("test@gmail.com", "+76584936574", address);
    assertThatThrownBy(() -> new Lead(null, contact, "TestCompany", LeadStatus.NEW, LeadIndustry.IT))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Id");
  }
}
