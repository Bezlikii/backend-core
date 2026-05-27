package ru.mentee.power.crm.educational.storage;

import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LeadStorageTest {

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
  void shouldAddLeadWhenLeadIsUnique() {
    LeadStorage storage = new LeadStorage();
    Lead uniqueLead = createLead(UUID.randomUUID(), "ivan@mail.ru", "+7123", "TechCorp", LeadStatus.NEW);

    boolean added = storage.add(uniqueLead);

    assertThat(added).isTrue();
    assertThat(storage.size()).isEqualTo(1);
    assertThat(storage.findAll()).containsExactly(uniqueLead);
  }

  @Test
  void shouldRejectDuplicateWhenEmailAlreadyExists() {
    LeadStorage storage = new LeadStorage();
    Lead existingLead = createLead(UUID.randomUUID(), "ivan@mail.ru", "+7123", "TechCorp", LeadStatus.NEW);
    Lead duplicateLead = createLead(UUID.randomUUID(), "ivan@mail.ru", "+7456", "Other", LeadStatus.NEW);
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
      storage.add(createLead(UUID.randomUUID(), "lead" + index + "@mail.ru", "+7000", "Company", LeadStatus.NEW));
    }

    Lead hundredFirstLead = createLead(UUID.randomUUID(), "lead101@mail.ru", "+7001", "Company", LeadStatus.NEW);

    assertThatThrownBy(() -> storage.add(hundredFirstLead))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Storage is full");
  }

  @Test
  void shouldReturnOnlyAddedLeadsWhenFindAllCalled() {
    LeadStorage storage = new LeadStorage();
    Lead firstLead = createLead(UUID.randomUUID(), "ivan@mail.ru", "+7123", "TechCorp", LeadStatus.NEW);
    Lead secondLead = createLead(UUID.randomUUID(), "maria@startup.io", "+7456", "StartupLab", LeadStatus.NEW);
    storage.add(firstLead);
    storage.add(secondLead);

    Lead[] result = storage.findAll();

    assertThat(result).hasSize(2);
    assertThat(result).containsExactly(firstLead, secondLead);
  }

  @Test
  void shouldPreventStringConfusionWhenUsingUUID() {
    UUID uuid = UUID.randomUUID();
    Lead lead1 = createLead(uuid, "test@gmail.com", "+7000000000", "Company", LeadStatus.NEW);
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
