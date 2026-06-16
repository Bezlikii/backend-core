package ru.mentee.power.crm.spring;

import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.spring.service.LeadService;

import java.util.List;
import java.util.UUID;

public class MockLeadService extends LeadService {
  private final List<Lead> mockLeads;

  public MockLeadService() {
    super(null);
    Address address = new Address("Moscow", "Tverskaya", "798332");
    Contact contact = new Contact("test@email.com", "+79168882233", address);
    Contact contact2 = new Contact("test2@email.com", "+79168889990066", address);
    this.mockLeads = List.of(
        new Lead(UUID.randomUUID(), contact, "testCompany", LeadStatus.NEW),
        new Lead(UUID.randomUUID(), contact2, "testCompany2", LeadStatus.CONTACTED)
    );
  }

  @Override
  public List<Lead> findAll() {
    return mockLeads;
  }
}
