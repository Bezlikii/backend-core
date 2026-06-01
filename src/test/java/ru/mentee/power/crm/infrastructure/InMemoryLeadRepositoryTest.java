package ru.mentee.power.crm.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;

class InMemoryLeadRepositoryTest {

  private Address createAddress() {
    return new Address("Moscow", "Izmailovskaya", "456324");
  }

  private Contact createContact(String email, String phone) {
    return new Contact(email, phone, createAddress());
  }

  private Lead createLead(UUID id, String email, String phone, String company, LeadStatus status) {
    return new Lead(id, createContact(email, phone), company, status);
  }

  @Test
  void shouldSaveLeadWhenLeadIsUnique() {
    InMemoryLeadRepository repository = new InMemoryLeadRepository();
    UUID id = UUID.randomUUID();
    Lead lead = createLead(id, "test@gmail.com", "+79256743845", "TestCompany", LeadStatus.NEW);
    repository.save(lead);

    assertThat(repository.findById(id)).contains(lead);
  }

  @Test
  void shouldNotSaveDuplicateLead() {
    InMemoryLeadRepository repository = new InMemoryLeadRepository();
    UUID id = UUID.randomUUID();
    Lead lead1 = createLead(id, "test@gmail.com", "+79256743845", "TestCompany", LeadStatus.NEW);
    Lead lead2 = createLead(id, "test@gmail.com", "+79256743845", "TestCompany", LeadStatus.NEW);
    repository.save(lead1);
    repository.save(lead2);

    assertThat(repository.findAll()).hasSize(1);
  }

  @Test
  void shouldDeleteLeadWhenIdExists() {
    InMemoryLeadRepository repository = new InMemoryLeadRepository();
    UUID id = UUID.randomUUID();
    Lead lead = createLead(id, "test@gmail.com", "+79256743845", "TestCompany", LeadStatus.NEW);

    repository.save(lead);
    assertThat(repository.findById(id)).contains(lead);
    repository.delete(id);
    assertThat(repository.findAll()).isEmpty();
  }

  @Test
  void shouldDoNothingWhenDeleteNonExistentId() {
    InMemoryLeadRepository repository = new InMemoryLeadRepository();
    UUID id = UUID.randomUUID();
    Lead lead = createLead(id, "test@gmail.com", "+79256743845", "TestCompany", LeadStatus.NEW);

    repository.save(lead);
    assertThat(repository.findById(id)).contains(lead);
    repository.delete(UUID.randomUUID());
    assertThat(repository.findAll()).hasSize(1);
  }

  @Test
  void shouldFindLeadByIdWhenIdExists() {
    InMemoryLeadRepository repository = new InMemoryLeadRepository();
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();
    UUID id3 = UUID.randomUUID();
    Lead lead1 = createLead(id1, "test@gmail.com", "+79256743845", "TestCompany", LeadStatus.NEW);
    Lead lead2 = createLead(
        id2, "test2@gmail.com", "+79567845656", "Test2Company", LeadStatus.QUALIFIED);
    Lead lead3 = createLead(id3, "test3@gmail.com", "+76453896453", "TestCompany", LeadStatus.NEW);
    repository.save(lead1);
    repository.save(lead2);
    repository.save(lead3);

    assertThat(repository.findById(id2)).contains(lead2);
  }

  @Test
  void shouldReturnEmptyWhenFindByIdNotFound() {
    InMemoryLeadRepository repository = new InMemoryLeadRepository();
    UUID id = UUID.randomUUID();
    Lead lead = createLead(id, "test@gmail.com", "+79256743845", "TestCompany", LeadStatus.NEW);
    repository.save(lead);

    assertThat(repository.findById(UUID.randomUUID())).isEqualTo(Optional.empty());
  }

  @Test
  void shouldReturnAllLeads() {
    InMemoryLeadRepository repository = new InMemoryLeadRepository();
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();
    UUID id3 = UUID.randomUUID();
    Lead lead1 = createLead(id1, "test@gmail.com", "+79256743845", "TestCompany", LeadStatus.NEW);
    Lead lead2 = createLead(
        id2, "test2@gmail.com", "+79567845656", "Test2Company", LeadStatus.QUALIFIED);
    Lead lead3 = createLead(id3, "test3@gmail.com", "+76453896453", "TestCompany", LeadStatus.NEW);
    repository.save(lead1);
    repository.save(lead2);
    repository.save(lead3);

    assertThat(repository.findAll()).containsExactly(lead1, lead2, lead3).hasSize(3);
  }

  @Test
  void shouldReturnEmptyListWhenEmpty() {
    InMemoryLeadRepository repository = new InMemoryLeadRepository();

    assertThat(repository.findAll()).isEmpty();
  }

  @Test
  void shouldReturnDefensiveCopy() {
    InMemoryLeadRepository repository = new InMemoryLeadRepository();
    UUID id = UUID.randomUUID();
    Lead lead = createLead(id, "test@gmail.com", "+79256743845", "TestCompany", LeadStatus.NEW);
    repository.save(lead);

    List<Lead> result = repository.findAll();
    result.clear();

    assertThat(repository.findAll()).hasSize(1);
  }

  @Test
  void shouldFindByEmailWhenEmailExists() {
    InMemoryLeadRepository repository = new InMemoryLeadRepository();
    Lead lead = createLead(
        UUID.randomUUID(), "test@gmail.com", "+79256743845", "TestCompany", LeadStatus.NEW);
    repository.save(lead);

    Optional<Lead> result = repository.findByEmail("test@gmail.com");

    assertThat(result).isPresent();
    assertThat(result.get().company()).isEqualTo("TestCompany");
  }

  @Test
  void shouldReturnEmptyWhenFindByEmailNotFound() {
    InMemoryLeadRepository repository = new InMemoryLeadRepository();
    Lead lead = createLead(
        UUID.randomUUID(), "test@gmail.com", "+79256743845", "TestCompany", LeadStatus.NEW);
    repository.save(lead);

    Optional<Lead> result = repository.findByEmail("nonexistent@gmail.com");

    assertThat(result).isEmpty();
  }

  @Test
  void shouldHandleDuplicateEmailsWithDifferentIds() {
    InMemoryLeadRepository repository = new InMemoryLeadRepository();
    Lead lead1 = createLead(
        UUID.randomUUID(), "test@gmail.com", "+79256743845", "TestCompany", LeadStatus.NEW);
    Lead lead2 = createLead(
        UUID.randomUUID(), "test@gmail.com", "+79256743845", "OtherCompany", LeadStatus.QUALIFIED);
    repository.save(lead1);
    repository.save(lead2);

    assertThat(repository.findAll()).hasSize(2);

    Optional<Lead> result = repository.findByEmail("test@gmail.com");
    assertThat(result).isPresent();
    assertThat(result.get().id()).isEqualTo(lead1.id());
  }

  @Test
  void shouldReturnDefensiveCopyFromFindAll() {
    InMemoryLeadRepository repository = new InMemoryLeadRepository();
    UUID id = UUID.randomUUID();
    Lead lead = createLead(id, "test@gmail.com", "+79256743845", "TestCompany", LeadStatus.NEW);
    repository.save(lead);

    List<Lead> result = repository.findAll();
    result.clear();

    assertThat(repository.findAll()).hasSize(1);
  }
}
