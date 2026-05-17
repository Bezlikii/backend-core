package ru.mentee.power.crm.domain;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class LeadEqualsHashCodeTest {

  @Test
  void shouldBeReflexiveWhenEqualsCalledOnSameObject() {
    Lead lead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");

    assertThat(lead).isEqualTo(lead);
  }

  @Test
  void shouldBeSymmetricWhenEqualsCalledOnTwoObjects() {
    Lead firstLead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");
    Lead secondLead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");

    assertThat(firstLead).isEqualTo(secondLead);
    assertThat(secondLead).isEqualTo(firstLead);
  }

  @Test
  void shouldBeTransitiveWhenEqualsChainOfThreeObjects() {
    Lead firstLead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");
    Lead secondLead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");
    Lead thirdLead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");

    assertThat(firstLead).isEqualTo(secondLead);
    assertThat(secondLead).isEqualTo(thirdLead);
    assertThat(firstLead).isEqualTo(thirdLead);
  }

  @Test
  void shouldBeConsistentWhenEqualsCalledMultipleTimes() {
    Lead firstLead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");
    Lead secondLead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");

    assertThat(firstLead).isEqualTo(secondLead);
    assertThat(firstLead).isEqualTo(secondLead);
    assertThat(firstLead).isEqualTo(secondLead);
  }

  @Test
  void shouldReturnFalseWhenEqualsComparedWithNull() {
    Lead lead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");

    assertThat(lead).isNotEqualTo(null);
  }

  @Test
  void shouldHaveSameHashCodeWhenObjectsAreEqual() {
    Lead firstLead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");
    Lead secondLead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");

    assertThat(firstLead).isEqualTo(secondLead);
    assertThat(firstLead.hashCode()).isEqualTo(secondLead.hashCode());
  }

  @Test
  void shouldWorkInHashMapWhenLeadUsedAsKey() {
    Lead keyLead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");
    Lead lookupLead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");

    Map<Lead, String> map = new HashMap<>();
    map.put(keyLead, "CONTACTED");

    String status = map.get(lookupLead);

    assertThat(status).isEqualTo("CONTACTED");
  }

  @Test
  void shouldNotBeEqualWhenIdsAreDifferent() {
    Lead firstLead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");
    Lead differentLead = new Lead("2", "ivan@mail.ru", "+7123", "TechCorp", "NEW");

    assertThat(firstLead).isNotEqualTo(differentLead);
  }

  @Test
  void shouldReturnFalseWhenComparedWithDifferentClass() {
    Lead lead = new Lead("1", "ivan@mail.ru", "+7123", "TechCorp", "NEW");
    assertThat(lead.equals("Not a Lead")).isFalse();
  }
}