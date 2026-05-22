package ru.mentee.power.crm.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.domain.Lead;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LeadRepositoryTest {

  private Lead createLead(UUID id) {
    Address address = new Address("Moscow", "Izmailovskaya", "876354");
    Contact contact = new Contact("test@gmail.com", "+76584936574", address);
    return new Lead(id, contact, "TestCompany", "NEW");
  }

  @Test
  @DisplayName("Should automatically deduplicate leads by id")
  void shouldDeduplicateLeadsById() {
    LeadRepository repository = new LeadRepository();
    UUID id = UUID.randomUUID();
    boolean firstResult = repository.add(createLead(id));
    boolean secondResult = repository.add(createLead(id));

    assertThat(firstResult).isTrue();
    assertThat(secondResult).isFalse();
    assertThat(repository.findAll()).hasSize(1);
  }

  @Test
  @DisplayName("Should allow different leads with different ids")
  void shouldAllowDifferentLeads() {
    LeadRepository repository = new LeadRepository();
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();
    boolean firstResult = repository.add(createLead(id1));
    boolean secondResult = repository.add(createLead(id2));

    assertThat(firstResult).isTrue();
    assertThat(secondResult).isTrue();
    assertThat(repository.size()).isEqualTo(2);
  }

  @Test
  @DisplayName("Should find existing lead through contains")
  void shouldFindExistingLead() {
    LeadRepository repository = new LeadRepository();
    UUID id = UUID.randomUUID();
    Lead lead = createLead(id);
    repository.add(lead);

    assertThat(repository.contains(lead)).isTrue();
  }

  @Test
  @DisplayName("Should return unmodifiable set from findAll")
  void shouldReturnUnmodifiableSet() {
    LeadRepository repository = new LeadRepository();
    UUID id = UUID.randomUUID();
    repository.add(createLead(id));
    Set<Lead> result = repository.findAll();

    assertThatThrownBy(() -> result.add(createLead(UUID.randomUUID())))
        .isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  @DisplayName("Should perform contains() faster than ArrayList")
  void shouldPerformFasterThanArrayList() {
    Set<Lead> set = new HashSet<>();
    List<Lead> list = new ArrayList<>();

    for (int i = 0; i < 10000; i++) {
      Lead lead = createLead(UUID.randomUUID());
      set.add(lead);
      list.add(lead);
    }

    Lead searchLead = createLead(UUID.randomUUID());

    long setStart = System.nanoTime();
    for (int i = 0; i < 1000; i++) {
      set.contains(searchLead);
    }
    long setDuration = System.nanoTime() - setStart;

    long listStart = System.nanoTime();
    for (int i = 0; i < 1000; i++) {
      list.contains(searchLead);
    }
    long listDuration = System.nanoTime() - listStart;

    assertThat(setDuration).isLessThan(listDuration / 100);
  }
}