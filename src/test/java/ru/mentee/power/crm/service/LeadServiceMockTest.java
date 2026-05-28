package ru.mentee.power.crm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import java.util.UUID;

import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.domain.Repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LeadServiceMockTest {

  @Mock
  private Repository<Lead> mockRepository;

  private LeadService service;

  @BeforeEach
  void setUp() {
    service = new LeadService(mockRepository);
  }

  @Test
  void shouldCallRepositorySaveWhenAddingNewLead() {
    when(mockRepository.findByEmail(anyString()))
        .thenReturn(Optional.empty());
    when(mockRepository.save(any(Lead.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Address address = new Address("Moscow", "Eliseevskaya 15", "987465");
    Lead result = service.addLead("new@example.com", "+79169876453",
        address, "Company", LeadStatus.NEW);

    verify(mockRepository, times(1)).save(any(Lead.class));

    assertThat(result.contact().email()).isEqualTo("new@example.com");
  }

  @Test
  void shouldNotCallSaveWhenEmailExists() {
    Address address = new Address("Moscow", "Eliseevskaya 15", "987465");
    Contact contact = new Contact("test@gmail.com", "+79167654563245", address);
    Lead expectedLead = new Lead(UUID.randomUUID(), contact, "TestCompany", LeadStatus.CONVERTED);

    when(mockRepository.findByEmail("test@gmail.com"))
        .thenReturn(Optional.of(expectedLead));

    Address address2 = new Address("Voronesh", "Leningradskaya 10", "746283");

    assertThatThrownBy(() ->
        service.addLead("test@gmail.com", "+79169854637", address2, "Test2Company", LeadStatus.NEW))
        .isInstanceOf(IllegalStateException.class);

    verify(mockRepository, never()).save(any(Lead.class));
  }

  @Test
  void shouldCallFindByEmailBeforeSave() {
    when(mockRepository.findByEmail(anyString()))
        .thenReturn(Optional.empty());
    when(mockRepository.save((any(Lead.class))))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Address address = new Address("Moscow", "Eliseevskaya 15", "987465");
    service.addLead("new@example.com", "+79169876453", address, "Company", LeadStatus.NEW);

    var inOrder = inOrder(mockRepository);
    inOrder.verify(mockRepository).findByEmail("new@example.com");
    inOrder.verify(mockRepository).save(any(Lead.class));
  }
}
