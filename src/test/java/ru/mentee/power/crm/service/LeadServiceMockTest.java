package ru.mentee.power.crm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.domain.CrudRepository;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;

@ExtendWith(MockitoExtension.class)
public class LeadServiceMockTest {

  @Mock
  private CrudRepository<Lead> mockCrudRepository;

  private LeadService service;

  @BeforeEach
  void setUp() {
    service = new LeadService(mockCrudRepository);
  }

  @Test
  void shouldCallRepositorySaveWhenAddingNewLead() {
    when(mockCrudRepository.findByEmail(anyString()))
        .thenReturn(Optional.empty());
    when(mockCrudRepository.save(any(Lead.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Address address = new Address("Moscow", "Eliseevskaya 15", "987465");
    Lead result = service.addLead("new@example.com", "+79169876453",
        address, "Company", LeadStatus.NEW);

    verify(mockCrudRepository, times(1)).save(any(Lead.class));

    assertThat(result.contact().email()).isEqualTo("new@example.com");
  }

  @Test
  void shouldNotCallSaveWhenEmailExists() {
    Address address = new Address("Moscow", "Eliseevskaya 15", "987465");
    Contact contact = new Contact("test@gmail.com", "+79167654563245", address);
    Lead expectedLead = new Lead(UUID.randomUUID(), contact, "TestCompany", LeadStatus.QUALIFIED);

    when(mockCrudRepository.findByEmail("test@gmail.com"))
        .thenReturn(Optional.of(expectedLead));

    Address address2 = new Address("Voronesh", "Leningradskaya 10", "746283");

    assertThatThrownBy(() ->
        service.addLead("test@gmail.com", "+79169854637", address2, "Test2Company", LeadStatus.NEW))
        .isInstanceOf(IllegalStateException.class);

    verify(mockCrudRepository, never()).save(any(Lead.class));
  }

  @Test
  void shouldCallFindByEmailBeforeSave() {
    when(mockCrudRepository.findByEmail(anyString()))
        .thenReturn(Optional.empty());
    when(mockCrudRepository.save((any(Lead.class))))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Address address = new Address("Moscow", "Eliseevskaya 15", "987465");
    service.addLead("new@example.com", "+79169876453", address, "Company", LeadStatus.NEW);

    var inOrder = inOrder(mockCrudRepository);
    inOrder.verify(mockCrudRepository).findByEmail("new@example.com");
    inOrder.verify(mockCrudRepository).save(any(Lead.class));
  }
}
