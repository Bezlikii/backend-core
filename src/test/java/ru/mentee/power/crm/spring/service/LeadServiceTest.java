package ru.mentee.power.crm.spring.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.spring.repository.LeadRepository;

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
        address, "Company 2", LeadStatus.CONTACTED);

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
        address, "Company", LeadStatus.CONTACTED);
    Lead convertedLead = service.addLead("converted@example.com", "+79163333333",
        address, "Company", LeadStatus.QUALIFIED);

    assertThat(newLead.status()).isEqualTo(LeadStatus.NEW);
    assertThat(qualifiedLead.status()).isEqualTo(LeadStatus.CONTACTED);
    assertThat(convertedLead.status()).isEqualTo(LeadStatus.QUALIFIED);
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
  void shouldThrowExceptionWhenCompanyIsNull() {
    Address address = new Address("Moscow", "Izmailovskaya", "123456");

    assertThatThrownBy(() ->
        service.addLead("test@example.com", "+79161234567",
            address, null, LeadStatus.NEW)
    )
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Company");
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

  @ParameterizedTest(name = "[{index}] status={0}, expectedSize={1}")
  @CsvSource({
      "NEW,        3",
      "CONTACTED,  5",
      "QUALIFIED,  2"
  })
  void shouldFilterLeadsByStatus(String searchStatus, int expectedSize) {
    Address address = new Address("Moscow", "Test", "123456");

    service.addLead("new1@test.com", "+79991111111", address, "Company", LeadStatus.NEW);
    service.addLead("new2@test.com", "+79991111112", address, "Company", LeadStatus.NEW);
    service.addLead("new3@test.com", "+79991111113", address, "Company", LeadStatus.NEW);
    service.addLead("ct1@test.com", "+79991111114", address, "Company", LeadStatus.CONTACTED);
    service.addLead("ct2@test.com", "+79991111115", address, "Company", LeadStatus.CONTACTED);
    service.addLead("ct3@test.com", "+79991111116", address, "Company", LeadStatus.CONTACTED);
    service.addLead("ct4@test.com", "+79991111117", address, "Company", LeadStatus.CONTACTED);
    service.addLead("ct5@test.com", "+79991111118", address, "Company", LeadStatus.CONTACTED);
    service.addLead("ql1@test.com", "+79991111119", address, "Company", LeadStatus.QUALIFIED);
    service.addLead("ql2@test.com", "+79991111120", address, "Company", LeadStatus.QUALIFIED);

    LeadStatus search = LeadStatus.valueOf(searchStatus);
    List<Lead> result = service.findByStatus(search);

    assertThat(result).hasSize(expectedSize);
    assertThat(result).allMatch(lead -> lead.status().equals(search));
  }

  @Test
  void shouldReturnEmptyListWhenNoLeadsWithStatus() {
    Address address = new Address("Moscow", "Test", "123456");

    service.addLead("new1@test.com", "+79991111111", address, "Company", LeadStatus.NEW);
    service.addLead("new2@test.com", "+79991111112", address, "Company", LeadStatus.CONTACTED);

    List<Lead> result = service.findByStatus(LeadStatus.QUALIFIED);

    assertThat(result).hasSize(0);
  }
}
