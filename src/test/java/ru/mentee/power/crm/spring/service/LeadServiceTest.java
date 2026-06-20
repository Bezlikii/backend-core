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
    Lead result = service.addLead("Test User", "test@example.com", "+79161234567",
        address, "Test Company", LeadStatus.NEW);

    assertThat(result).isNotNull();
    assertThat(result.contact().name()).isEqualTo("Test User");
    assertThat(result.contact().email()).isEqualTo("test@example.com");
    assertThat(result.contact().phone()).isEqualTo("+79161234567");
    assertThat(result.company()).isEqualTo("Test Company");
    assertThat(result.status()).isEqualTo(LeadStatus.NEW);
    assertThat(result.id()).isNotNull();
  }

  @Test
  void shouldThrowExceptionWhenEmailAlreadyExists() {
    Address address = new Address("Moscow", "Izmailovskaya", "123456");
    service.addLead("User", "duplicate@example.com", "+79161234567",
        address, "First Company", LeadStatus.NEW);

    assertThatThrownBy(() ->
        service.addLead("User", "duplicate@example.com", "+79169876543",
            address, "Second Company", LeadStatus.NEW)
    )
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Lead with email already exists");
  }

  @Test
  void shouldFindAllLeads() {
    Address address = new Address("Moscow", "Izmailovskaya", "123456");
    service.addLead("User", "one@example.com", "+79161111111",
        address, "Company 1", LeadStatus.NEW);
    service.addLead("User", "two@example.com", "+79162222222",
        address, "Company 2", LeadStatus.CONTACTED);

    List<Lead> result = service.findAll();

    assertThat(result).hasSize(2);
  }

  @Test
  void shouldFindLeadById() {
    Address address = new Address("Moscow", "Izmailovskaya", "123456");
    Lead created = service.addLead("User", "find@example.com", "+79161234567",
        address, "Company", LeadStatus.NEW);

    Optional<Lead> result = service.findById(created.id());

    assertThat(result).isPresent();
    assertThat(result.get().contact().email()).isEqualTo("find@example.com");
  }

  @Test
  void shouldFindLeadByEmail() {
    Address address = new Address("Moscow", "Izmailovskaya", "123456");
    service.addLead("User", "search@example.com", "+79161234567",
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

    Lead newLead = service.addLead("User", "new@example.com", "+79161111111",
        address, "Company", LeadStatus.NEW);
    Lead qualifiedLead = service.addLead("User", "qualified@example.com", "+79162222222",
        address, "Company", LeadStatus.CONTACTED);
    Lead convertedLead = service.addLead("User", "converted@example.com", "+79163333333",
        address, "Company", LeadStatus.QUALIFIED);

    assertThat(newLead.status()).isEqualTo(LeadStatus.NEW);
    assertThat(qualifiedLead.status()).isEqualTo(LeadStatus.CONTACTED);
    assertThat(convertedLead.status()).isEqualTo(LeadStatus.QUALIFIED);
  }

  @Test
  void shouldThrowExceptionWhenEmailIsNull() {
    Address address = new Address("Moscow", "Izmailovskaya", "123456");

    assertThatThrownBy(() ->
        service.addLead("User", null, "+79161234567", address, "Company", LeadStatus.NEW)
    )
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Email");
  }

  @Test
  void shouldThrowExceptionWhenCompanyIsNull() {
    Address address = new Address("Moscow", "Izmailovskaya", "123456");

    assertThatThrownBy(() ->
        service.addLead("User", "test@example.com", "+79161234567",
            address, null, LeadStatus.NEW)
    )
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Company");
  }

  @Test
  void shouldThrowExceptionWhenAddressIsNull() {
    assertThatThrownBy(() ->
        service.addLead("User", "test@example.com", "+79161234567",
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

    service.addLead("User", "new1@test.com", "+79991111111", address, "Company", LeadStatus.NEW);
    service.addLead("User", "new2@test.com", "+79991111112", address, "Company", LeadStatus.NEW);
    service.addLead("User", "new3@test.com", "+79991111113", address, "Company", LeadStatus.NEW);
    service.addLead("User", "ct1@test.com", "+79991111114", address, "Company", LeadStatus.CONTACTED);
    service.addLead("User", "ct2@test.com", "+79991111115", address, "Company", LeadStatus.CONTACTED);
    service.addLead("User", "ct3@test.com", "+79991111116", address, "Company", LeadStatus.CONTACTED);
    service.addLead("User", "ct4@test.com", "+79991111117", address, "Company", LeadStatus.CONTACTED);
    service.addLead("User", "ct5@test.com", "+79991111118", address, "Company", LeadStatus.CONTACTED);
    service.addLead("User", "ql1@test.com", "+79991111119", address, "Company", LeadStatus.QUALIFIED);
    service.addLead("User", "ql2@test.com", "+79991111120", address, "Company", LeadStatus.QUALIFIED);

    LeadStatus search = LeadStatus.valueOf(searchStatus);
    List<Lead> result = service.findByStatus(search);

    assertThat(result).hasSize(expectedSize);
    assertThat(result).allMatch(lead -> lead.status().equals(search));
  }

  @Test
  void shouldReturnEmptyListWhenNoLeadsWithStatus() {
    Address address = new Address("Moscow", "Test", "123456");

    service.addLead("User", "new1@test.com", "+79991111111", address, "Company", LeadStatus.NEW);
    service.addLead("User", "new2@test.com", "+79991111112", address, "Company", LeadStatus.CONTACTED);

    List<Lead> result = service.findByStatus(LeadStatus.QUALIFIED);

    assertThat(result).hasSize(0);
  }

  @Test
  void shouldFindLeadsBySearchInName() {
    Address address = new Address("Moscow", "Test", "123456");
    service.addLead("Ivan Petrov", "ivan@example.com", "+79991111111", address, "Company", LeadStatus.NEW);
    service.addLead("John Smith", "john@example.com", "+79992222222", address, "Company", LeadStatus.NEW);

    List<Lead> result = service.findLeads("ivan", null);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).contact().name()).isEqualTo("Ivan Petrov");
  }

  @Test
  void shouldFindLeadsBySearchInEmail() {
    Address address = new Address("Moscow", "Test", "123456");
    service.addLead("User One", "ivan@example.com", "+79991111111", address, "Company", LeadStatus.NEW);
    service.addLead("User Two", "john@example.com", "+79992222222", address, "Company", LeadStatus.NEW);

    List<Lead> result = service.findLeads("ivan@", null);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).contact().email()).isEqualTo("ivan@example.com");
  }

  @Test
  void shouldFindLeadsBySearchCaseInsensitive() {
    Address address = new Address("Moscow", "Test", "123456");
    service.addLead("Ivan Petrov", "ivan@example.com", "+79991111111", address, "Company", LeadStatus.NEW);
    service.addLead("John Smith", "john@example.com", "+79992222222", address, "Company", LeadStatus.NEW);

    List<Lead> result = service.findLeads("IVAN", null);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).contact().name()).isEqualTo("Ivan Petrov");
  }

  @Test
  void shouldFindLeadsByStatus() {
    Address address = new Address("Moscow", "Test", "123456");
    service.addLead("User One", "one@example.com", "+79991111111", address, "Company", LeadStatus.NEW);
    service.addLead("User Two", "two@example.com", "+79992222222", address, "Company", LeadStatus.CONTACTED);

    List<Lead> result = service.findLeads(null, "NEW");

    assertThat(result).hasSize(1);
    assertThat(result.get(0).status()).isEqualTo(LeadStatus.NEW);
  }

  @Test
  void shouldFindLeadsWithCombinedFilters() {
    Address address = new Address("Moscow", "Test", "123456");
    service.addLead("Ivan New", "ivan.new@example.com", "+79991111111", address, "Company", LeadStatus.NEW);
    service.addLead("Ivan Contacted", "ivan.contacted@example.com", "+79992222222", address, "Company", LeadStatus.CONTACTED);
    service.addLead("John New", "john.new@example.com", "+79993333333", address, "Company", LeadStatus.NEW);

    List<Lead> result = service.findLeads("ivan", "NEW");

    assertThat(result).hasSize(1);
    assertThat(result.get(0).contact().name()).isEqualTo("Ivan New");
    assertThat(result.get(0).status()).isEqualTo(LeadStatus.NEW);
  }

  @Test
  void shouldReturnAllLeadsWhenNoFilters() {
    Address address = new Address("Moscow", "Test", "123456");
    service.addLead("User One", "one@example.com", "+79991111111", address, "Company", LeadStatus.NEW);
    service.addLead("User Two", "two@example.com", "+79992222222", address, "Company", LeadStatus.CONTACTED);

    List<Lead> result = service.findLeads(null, null);

    assertThat(result).hasSize(2);
  }

  @Test
  void shouldReturnAllLeadsWhenEmptyFilters() {
    Address address = new Address("Moscow", "Test", "123456");
    service.addLead("User One", "one@example.com", "+79991111111", address, "Company", LeadStatus.NEW);
    service.addLead("User Two", "two@example.com", "+79992222222", address, "Company", LeadStatus.CONTACTED);

    List<Lead> result = service.findLeads("", "");

    assertThat(result).hasSize(2);
  }

  @Test
  void shouldReturnEmptyListWhenNoMatchesForSearch() {
    Address address = new Address("Moscow", "Test", "123456");
    service.addLead("User One", "one@example.com", "+79991111111", address, "Company", LeadStatus.NEW);

    List<Lead> result = service.findLeads("nonexistent", null);

    assertThat(result).isEmpty();
  }
}
