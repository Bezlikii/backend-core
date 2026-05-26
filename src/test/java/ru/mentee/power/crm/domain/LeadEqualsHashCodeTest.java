package ru.mentee.power.crm.domain;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class LeadEqualsHashCodeTest {

  private Address createAddress() {
    return new Address("Moscow", "Izmailovskaya", "876354");
  }

  private Contact createContact(String email, String phone) {
    return new Contact(email, phone, createAddress());
  }

  private Lead createLead(UUID id, String email, String phone, String company, LeadStatus status) {
    return new Lead(id, createContact(email, phone), company, status);
  }

  @Test
  void shouldBeReflexiveWhenEqualsCalledOnSameObject() {
    Lead lead = createLead(UUID.randomUUID(), "test@gmail.com", "+76584936574", "TestCompany", LeadStatus.NEW);

    assertThat(lead).isEqualTo(lead);
  }

  @Test
  void shouldBeSymmetricWhenEqualsCalledOnTwoObjects() {
    UUID sameId = UUID.randomUUID();

    Lead firstLead = createLead(sameId, "ivan@mail.ru", "+7123", "TechCorp", LeadStatus.NEW);
    Lead secondLead = createLead(sameId, "ivan@mail.ru", "+7123", "TechCorp", LeadStatus.NEW);

    assertThat(firstLead).isEqualTo(secondLead);
    assertThat(secondLead).isEqualTo(firstLead);
  }

  @Test
  void shouldBeTransitiveWhenEqualsChainOfThreeObjects() {
    UUID sameId = UUID.randomUUID();

    Lead firstLead = createLead(sameId, "ivan@mail.ru", "+7123", "TechCorp", LeadStatus.NEW);
    Lead secondLead = createLead(sameId, "ivan@mail.ru", "+7123", "TechCorp", LeadStatus.NEW);
    Lead thirdLead = createLead(sameId, "ivan@mail.ru", "+7123", "TechCorp", LeadStatus.NEW);

    assertThat(firstLead).isEqualTo(secondLead);
    assertThat(secondLead).isEqualTo(thirdLead);
    assertThat(firstLead).isEqualTo(thirdLead);
  }

  @Test
  void shouldBeConsistentWhenEqualsCalledMultipleTimes() {
    UUID sameId = UUID.randomUUID();

    Lead firstLead = createLead(sameId, "ivan@mail.ru", "+7123", "TechCorp", LeadStatus.NEW);
    Lead secondLead = createLead(sameId, "ivan@mail.ru", "+7123", "TechCorp", LeadStatus.NEW);

    assertThat(firstLead).isEqualTo(secondLead);
    assertThat(firstLead).isEqualTo(secondLead);
    assertThat(firstLead).isEqualTo(secondLead);
  }

  @Test
  void shouldReturnFalseWhenEqualsComparedWithNull() {
    Lead lead = createLead(UUID.randomUUID(), "ivan@mail.ru", "+7123", "TechCorp", LeadStatus.NEW);

    assertThat(lead).isNotEqualTo(null);
  }

  @Test
  void shouldHaveSameHashCodeWhenObjectsAreEqual() {
    UUID sameId = UUID.randomUUID();

    Lead firstLead = createLead(sameId, "ivan@mail.ru", "+7123", "TechCorp", LeadStatus.NEW);
    Lead secondLead = createLead(sameId, "ivan@mail.ru", "+7123", "TechCorp", LeadStatus.NEW);

    assertThat(firstLead).isEqualTo(secondLead);
    assertThat(firstLead.hashCode()).isEqualTo(secondLead.hashCode());
  }

  @Test
  void shouldWorkInHashMapWhenLeadUsedAsKey() {
    UUID sameId = UUID.randomUUID();

    Lead keyLead = createLead(sameId, "ivan@mail.ru", "+7123", "TechCorp", LeadStatus.NEW);
    Lead lookupLead = createLead(sameId, "ivan@mail.ru", "+7123", "TechCorp", LeadStatus.NEW);

    Map<Lead, String> map = new HashMap<>();
    map.put(keyLead, "CONTACTED");

    String status = map.get(lookupLead);

    assertThat(status).isEqualTo("CONTACTED");
  }

  @Test
  void shouldNotBeEqualWhenIdsAreDifferent() {
    Lead firstLead = createLead(UUID.randomUUID(), "ivan@mail.ru", "+7123", "TechCorp", LeadStatus.NEW);
    Lead differentLead = createLead(UUID.randomUUID(), "ivan@mail.ru", "+7123", "TechCorp", LeadStatus.NEW);

    assertThat(firstLead).isNotEqualTo(differentLead);
  }

  @Test
  void shouldReturnFalseWhenComparedWithDifferentClass() {
    Lead lead = createLead(UUID.randomUUID(), "ivan@mail.ru", "+7123", "TechCorp", LeadStatus.NEW);
    assertThat(lead.equals("Not a Lead")).isFalse();
  }
}
