package ru.mentee.power.crm.infrastructure;

import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.domain.Lead;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryLeadRepositoryTest {

  private Address createAddress() {
    return new Address("Moscow", "Izmailovskaya", "456324");
  }

  private Contact createContact(String email, String phone) {
    return new Contact(email, phone, createAddress());
  }

  private Lead createLead(UUID id, String email, String phone, String company, String status) {
    return new Lead(id, createContact(email, phone), company, status);
  }

  @Test
  void shouldAddLeadWhenLeadIsUnique() {
    InMemoryLeadRepository repository = new InMemoryLeadRepository();
    UUID id = UUID.randomUUID();
    Lead lead = createLead(id, "test@gmail.com", "+79256743845", "TestCompany", "NEW");
    repository.add(lead);

    assertThat(repository.findById(id)).contains(lead);
  }

  @Test
  void shouldDoNothingWhenLeadIsDuplicate() {
    InMemoryLeadRepository repository = new InMemoryLeadRepository();
    UUID id = UUID.randomUUID();
    Lead lead1 = createLead(id, "test@gmail.com", "+79256743845", "TestCompany", "NEW");
    Lead lead2 = createLead(id, "test@gmail.com", "+79256743845", "TestCompany", "NEW");
    repository.add(lead1);
    repository.add(lead2);

    assertThat(repository.findAll()).hasSize(1);
  }

  @Test
  void shouldRemoveLeadWhenIdExists() {
    InMemoryLeadRepository repository = new InMemoryLeadRepository();
    UUID id = UUID.randomUUID();
    Lead lead = createLead(id, "test@gmail.com", "+79256743845", "TestCompany", "NEW");

    repository.add(lead);
    assertThat(repository.findById(id)).contains(lead);
    repository.remove(id);
    assertThat(repository.findAll()).isEmpty();
  }

  @Test
  void shouldDoNothingWhenIdNotFound() {
    InMemoryLeadRepository repository = new InMemoryLeadRepository();
    UUID id = UUID.randomUUID();
    Lead lead = createLead(id, "test@gmail.com", "+79256743845", "TestCompany", "NEW");

    repository.add(lead);
    assertThat(repository.findById(id)).contains(lead);
    repository.remove(UUID.randomUUID());
    assertThat(repository.findAll()).hasSize(1);
  }

  @Test
  void shouldReturnLeadWhenIdExists() {
    InMemoryLeadRepository repository = new InMemoryLeadRepository();
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();
    UUID id3 = UUID.randomUUID();
    Lead lead1 = createLead(id1, "test@gmail.com", "+79256743845", "TestCompany", "NEW");
    Lead lead2 = createLead(id2, "test2@gmail.com", "+79567845656", "Test2Company", "QUALIFIED");
    Lead lead3 = createLead(id3, "test3@gmail.com", "+76453896453", "TestCompany", "NEW");
    repository.add(lead1);
    repository.add(lead2);
    repository.add(lead3);

    assertThat(repository.findById(id2)).contains(lead2);
  }

  @Test
  void shouldReturnEmptyWhenIdNotFound() {
    InMemoryLeadRepository repository = new InMemoryLeadRepository();
    UUID id = UUID.randomUUID();
    Lead lead = createLead(id, "test@gmail.com", "+79256743845", "TestCompany", "NEW");
    repository.add(lead);

    assertThat(repository.findById(UUID.randomUUID())).isEqualTo(Optional.empty());
  }

  @Test
  void shouldReturnAllLeadsWhenTheyExist() {
    InMemoryLeadRepository repository = new InMemoryLeadRepository();
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();
    UUID id3 = UUID.randomUUID();
    Lead lead1 = createLead(id1, "test@gmail.com", "+79256743845", "TestCompany", "NEW");
    Lead lead2 = createLead(id2, "test2@gmail.com", "+79567845656", "Test2Company", "QUALIFIED");
    Lead lead3 = createLead(id3, "test3@gmail.com", "+76453896453", "TestCompany", "NEW");
    repository.add(lead1);
    repository.add(lead2);
    repository.add(lead3);

    assertThat(repository.findAll()).containsExactly(lead1, lead2, lead3).hasSize(3);
  }

  @Test
  void shouldReturnEmptyListWhenNoLeads() {
    InMemoryLeadRepository repository = new InMemoryLeadRepository();

    assertThat(repository.findAll()).isEmpty();
  }

  @Test
  void shouldReturnDefensiveCopy() {
    InMemoryLeadRepository repository = new InMemoryLeadRepository();
    UUID id = UUID.randomUUID();
    Lead lead = createLead(id, "test@gmail.com", "+79256743845", "TestCompany", "NEW");
    repository.add(lead);

    List<Lead> result = repository.findAll();
    result.clear();

    assertThat(repository.findAll()).hasSize(1);
  }
}