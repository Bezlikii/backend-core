package ru.mentee.power.crm.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;

class LeadRepositoryTest {
  private LeadRepository repository;

  @BeforeEach
  void setUp() {
    repository = new LeadRepository();
  }

  @Test
  void shouldSaveAndFindLeadByIdWhenLeadSaved() {
    UUID id = UUID.randomUUID();
    Contact contact = new Contact("test@gmail.com", "+79167654382",
        new Address("Moscow", "Izmailovskaya", "123456"));
    Lead lead = new Lead(id, contact, "TestCompany", LeadStatus.NEW);
    repository.save(lead);
    assertThat(repository.findById(id)).contains(lead);
  }

  @Test
  void shouldReturnEmptyWhenLeadNotFound() {
    assertThat(repository.findById(UUID.randomUUID())).isEmpty();
  }

  @Test
  void shouldReturnAllLeadsWhenMultipleLeadsSaved() {
    Address address = new Address("Moscow", "Izmailovskaya", "123456");
    Lead lead1 = new Lead(UUID.randomUUID(),
        new Contact("lead1@test.com", "+7001", address), "Company1", LeadStatus.NEW);
    Lead lead2 = new Lead(UUID.randomUUID(),
        new Contact("lead2@test.com", "+7002", address), "Company2", LeadStatus.NEW);
    Lead lead3 = new Lead(UUID.randomUUID(),
        new Contact("lead3@test.com", "+7003", address), "Company3", LeadStatus.NEW);
    repository.save(lead1);
    repository.save(lead2);
    repository.save(lead3);
    assertThat(repository.findAll()).hasSize(3);
  }

  @Test
  void shouldDeleteLeadWhenLeadExists() {
    UUID id = UUID.randomUUID();
    Contact contact = new Contact("test@gmail.com", "+79167654382",
        new Address("Moscow", "Izmailovskaya", "123456"));
    Lead lead = new Lead(id, contact, "TestCompany", LeadStatus.NEW);
    repository.save(lead);
    repository.delete(id);
    assertThat(repository.findById(id)).isEmpty();
    assertThat(repository.findAll()).isEmpty();
  }

  @Test
  void shouldOverwriteLeadWhenSaveWithSameId() {
    UUID id = UUID.randomUUID();
    Address address = new Address("Moscow", "Izmailovskaya", "123456");
    Lead lead1 = new Lead(id, new Contact("test1@gmail.com", "+7001", address),
        "Company1", LeadStatus.NEW);
    Lead lead2 = new Lead(id, new Contact("test2@gmail.com", "+7002", address),
        "Company2", LeadStatus.QUALIFIED);
    repository.save(lead1);
    repository.save(lead2);
    assertThat(repository.findById(id)).contains(lead2);
    assertThat(repository.findAll()).hasSize(1);
  }

  @Test
  void shouldFindFasterWithMapThanWithListFilter() {
    List<Lead> leadList = new ArrayList<>();
    Lead targetLead = null;

    for (int i = 0; i < 1000; i++) {
      UUID id = UUID.randomUUID();
      Contact contact = new Contact(
          "email" + i + "@test.com",
          "+7" + i,
          new Address("City" + i, "Street" + i, "ZIP" + i)
      );
      Lead lead = new Lead(id, contact, "Company" + i, LeadStatus.NEW);
      repository.save(lead);
      leadList.add(lead);
      if (i == 500) {
        targetLead = lead;
      }
    }

    UUID targetId = targetLead.id();

    long mapStart = System.nanoTime();
    Optional<Lead> foundInMap = repository.findById(targetId);
    long mapDuration = System.nanoTime() - mapStart;

    long listStart = System.nanoTime();
    Lead foundInList = leadList.stream()
        .filter(lead -> lead.id().equals(targetId))
        .findFirst()
        .orElse(null);
    long listDuration = System.nanoTime() - listStart;

    assertThat(foundInMap).contains(foundInList);
    assertThat(listDuration).isGreaterThan(mapDuration * 10);

    System.out.println("Map поиск: " + mapDuration + " ns");
    System.out.println("List поиск: " + listDuration + " ns");
    System.out.println("Ускорение: " + (listDuration / mapDuration) + "x");
  }

  @Test
  void shouldSaveBothLeadsEvenWithSameEmailAndPhone() {
    Contact sharedContact = new Contact("ivan@mail.ru", "+79001234567",
        new Address("Moscow", "Tverskaya 1", "101000"));
    Lead originalLead = new Lead(UUID.randomUUID(), sharedContact, "Acme Corp", LeadStatus.NEW);
    Lead duplicateLead = new Lead(
        UUID.randomUUID(), sharedContact, "TechCorp", LeadStatus.QUALIFIED);

    repository.save(originalLead);
    repository.save(duplicateLead);

    assertThat(repository.findAll()).hasSize(2);
  }

  @Test
  void shouldFindByEmailWhenEmailExists() {
    Address address = new Address("Moscow", "Izmailovskaya", "123456");
    Lead lead = new Lead(UUID.randomUUID(),
        new Contact("test@gmail.com", "+79167654382", address), "TestCompany", LeadStatus.NEW);
    repository.save(lead);

    Optional<Lead> result = repository.findByEmail("test@gmail.com");

    assertThat(result).isPresent();
    assertThat(result.get().company()).isEqualTo("TestCompany");
  }

  @Test
  void shouldReturnEmptyWhenFindByEmailWithNonExistentEmail() {
    Address address = new Address("Moscow", "Izmailovskaya", "123456");
    Lead lead = new Lead(UUID.randomUUID(),
        new Contact("test@gmail.com", "+79167654382", address), "TestCompany", LeadStatus.NEW);
    repository.save(lead);

    Optional<Lead> result = repository.findByEmail("nonexistent@gmail.com");

    assertThat(result).isEmpty();
  }

  @Test
  void shouldDeleteAndUpdateEmailIndex() {
    Address address = new Address("Moscow", "Izmailovskaya", "123456");
    Lead lead = new Lead(UUID.randomUUID(),
        new Contact("test@gmail.com", "+79167654382", address), "TestCompany", LeadStatus.NEW);
    repository.save(lead);

    repository.delete(lead.id());

    Optional<Lead> result = repository.findByEmail("test@gmail.com");
    assertThat(result).isEmpty();
  }

  @Test
  void shouldReturnDefensiveCopy() {
    Address address = new Address("Moscow", "Izmailovskaya", "123456");
    Lead lead = new Lead(UUID.randomUUID(),
        new Contact("test@gmail.com", "+79167654382", address), "TestCompany", LeadStatus.NEW);
    repository.save(lead);

    List<Lead> result = repository.findAll();
    result.clear();

    assertThat(repository.findAll()).hasSize(1);
  }

  @Test
  void shouldHandleNullEmailInFindByEmail() {
    Address address = new Address("Moscow", "Izmailovskaya", "123456");
    Lead lead = new Lead(UUID.randomUUID(),
        new Contact("test@gmail.com", "+79167654382", address), "TestCompany", LeadStatus.NEW);
    repository.save(lead);

    Optional<Lead> result = repository.findByEmail(null);

    assertThat(result).isEmpty();
  }
}
