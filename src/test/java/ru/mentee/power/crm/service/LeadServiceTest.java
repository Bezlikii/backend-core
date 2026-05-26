package ru.mentee.power.crm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.repository.LeadRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LeadServiceTest {

  private LeadService service;
  private LeadRepository repository;

  @BeforeEach
  void setUp() {
    repository = new LeadRepository();
    service = new LeadService(repository);
  }

  @Test
  void shouldCreateLeadWhenEmailIsUnique() {
    Address address = new Address("Moscow", "Izmailovskaya", "123456");
    Lead result = service.addLead("test@example.com", "+79161234567",
        address, "Test Company", LeadStatus.NEW);

    assertThat(result).isNotNull();
    assertThat(result.contact().email()).isEqualTo("test@example.com");
    assertThat(result.contact().phone()).isEqualTo("+79161234567");
    assertThat(result.company()).isEqualTo("Test Company");
    assertThat(result.status()).isEqualTo(LeadStatus.NEW);
    assertThat(result.id()).isNotNull();
  }

  @Test
  void shouldThrowExceptionWhenEmailAlreadyExists() {
    Address address = new Address("Moscow", "Izmailovskaya", "123456");
    service.addLead("duplicate@example.com", "+79161234567",
        address, "First Company", LeadStatus.NEW);

    assertThatThrownBy(() ->
        service.addLead("duplicate@example.com", "+79169876543",
            address, "Second Company", LeadStatus.NEW)
    )
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Lead with email already exists");
  }

  @Test
  void shouldFindAllLeads() {
    Address address = new Address("Moscow", "Izmailovskaya", "123456");
    service.addLead("one@example.com", "+79161111111",
        address, "Company 1", LeadStatus.NEW);
    service.addLead("two@example.com", "+79162222222",
        address, "Company 2", LeadStatus.QUALIFIED);

    List<Lead> result = service.findAll();

    assertThat(result).hasSize(2);
  }

  @Test
  void shouldFindLeadById() {
    Address address = new Address("Moscow", "Izmailovskaya", "123456");
    Lead created = service.addLead("find@example.com", "+79161234567",
        address, "Company", LeadStatus.NEW);

    Optional<Lead> result = service.findById(created.id());

    assertThat(result).isPresent();
    assertThat(result.get().contact().email()).isEqualTo("find@example.com");
  }

  @Test
  void shouldFindLeadByEmail() {
    Address address = new Address("Moscow", "Izmailovskaya", "123456");
    service.addLead("search@example.com", "+79161234567",
        address, "Company", LeadStatus.NEW);

    Optional<Lead> result = service.findByEmail("search@example.com");

    assertThat(result).isPresent();
    assertThat(result.get().company()).isEqualTo("Company");
  }

  @Test
  void shouldReturnEmptyWhenLeadNotFound() {
    Optional<Lead> result = service.findByEmail("nonexistent@example.com");

    assertThat(result).isEmpty();
  }

  @Test
  void shouldCreateLeadWithDifferentStatuses() {
    Address address = new Address("Moscow", "Izmailovskaya", "123456");

    Lead newLead = service.addLead("new@example.com", "+79161111111",
        address, "Company", LeadStatus.NEW);
    Lead qualifiedLead = service.addLead("qualified@example.com", "+79162222222",
        address, "Company", LeadStatus.QUALIFIED);
    Lead convertedLead = service.addLead("converted@example.com", "+79163333333",
        address, "Company", LeadStatus.CONVERTED);

    assertThat(newLead.status()).isEqualTo(LeadStatus.NEW);
    assertThat(qualifiedLead.status()).isEqualTo(LeadStatus.QUALIFIED);
    assertThat(convertedLead.status()).isEqualTo(LeadStatus.CONVERTED);
  }

  @Test
  void shouldThrowExceptionWhenEmailIsNull() {
    Address address = new Address("Moscow", "Izmailovskaya", "123456");

    assertThatThrownBy(() ->
        service.addLead(null, "+79161234567", address, "Company", LeadStatus.NEW)
    )
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Email");
  }

  @Test
  void shouldCreateLeadWhenCompanyIsNull() {
    Address address = new Address("Moscow", "Izmailovskaya", "123456");

    Lead result = service.addLead("test@example.com", "+79161234567",
        address, null, LeadStatus.NEW);

    assertThat(result).isNotNull();
    assertThat(result.company()).isNull();
  }

  @Test
  void shouldThrowExceptionWhenAddressIsNull() {
    assertThatThrownBy(() ->
        service.addLead("test@example.com", "+79161234567",
            null, "Company", LeadStatus.NEW)
    )
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Address");
  }

  @Test
  void shouldReturnEmptyWhenFindByEmailWithNull() {
    Optional<Lead> result = service.findByEmail(null);

    assertThat(result).isEmpty();
  }
}
