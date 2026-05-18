package ru.mentee.power.crm.storage;

import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.domain.Lead;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LeadStorageTest {

  @Test
  void shouldAddLeadWhenLeadIsUnique() {
    LeadStorage storage = new LeadStorage();
    Lead uniqueLead = new Lead(UUID.randomUUID(), "ivan@mail.ru", "+7123", "TechCorp", "NEW");

    boolean added = storage.add(uniqueLead);

    assertThat(added).isTrue();
    assertThat(storage.size()).isEqualTo(1);
    assertThat(storage.findAll()).containsExactly(uniqueLead);
  }

  @Test
  void shouldRejectDuplicateWhenEmailAlreadyExists() {
    LeadStorage storage = new LeadStorage();
    Lead existingLead = new Lead(UUID.randomUUID(), "ivan@mail.ru", "+7123", "TechCorp", "NEW");
    Lead duplicateLead = new Lead(UUID.randomUUID(), "ivan@mail.ru", "+7456", "Other", "NEW");
    storage.add(existingLead);

    boolean added = storage.add(duplicateLead);

    assertThat(added).isFalse();
    assertThat(storage.size()).isEqualTo(1);
    assertThat(storage.findAll()).containsExactly(existingLead);
  }

  @Test
  void shouldThrowExceptionWhenStorageIsFull() {
    LeadStorage storage = new LeadStorage();
    for (int index = 0; index < 100; index++) {
      storage.add(new Lead(UUID.randomUUID(), "lead" + index + "@mail.ru", "+7000", "Company", "NEW"));
    }

    Lead hundredFirstLead = new Lead(UUID.randomUUID(), "lead101@mail.ru", "+7001", "Company", "NEW");

    assertThatThrownBy(() -> storage.add(hundredFirstLead))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Storage is full");
  }

  @Test
  void shouldReturnOnlyAddedLeadsWhenFindAllCalled() {
    LeadStorage storage = new LeadStorage();
    Lead firstLead = new Lead(UUID.randomUUID(), "ivan@mail.ru", "+7123", "TechCorp", "NEW");
    Lead secondLead = new Lead(UUID.randomUUID(), "maria@startup.io", "+7456", "StartupLab", "NEW");
    storage.add(firstLead);
    storage.add(secondLead);

    Lead[] result = storage.findAll();

    assertThat(result).hasSize(2);
    assertThat(result).containsExactly(firstLead, secondLead);
  }

  @Test
  void shouldPreventStringConfusionWhenUsingUUID() {
    UUID uuid = UUID.randomUUID();
    Lead lead1 = new Lead(uuid, "test@gmail.com", "+7000000000", "Company", "NEW");
    LeadStorage storage = new LeadStorage();
    storage.add(lead1);

    Lead found = storage.findById(uuid);
    assertThat(found).isEqualTo(lead1);

    // Это НЕ скомпилируется:
    // storage.findById("123");  // ERROR: String != UUID
  }

  @Test
  void shouldReturnNullWhenStorageIsEmpty() {
    UUID uuid = UUID.randomUUID();
    LeadStorage storage = new LeadStorage();
    Lead found = storage.findById(uuid);
    assertThat(found).isNull();
  }
}