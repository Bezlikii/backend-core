package ru.mentee.power.crm.service;

import org.springframework.stereotype.Service;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.domain.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LeadService {

  private final CrudRepository<Lead> repository;

  public LeadService(CrudRepository<Lead> repository) {
    this.repository = repository;
  }

  public Lead addLead(String email, String phone, Address address,
      String company, LeadStatus status) {
    if (repository.findByEmail(email).isPresent()) {
      throw new IllegalStateException("Lead with email already exists: " + email);
    }

    Contact contact = new Contact(email, phone, address);
    Lead lead = new Lead(UUID.randomUUID(), contact, company, status);
    return repository.save(lead);
  }

  public List<Lead> findAll() {
    return repository.findAll();
  }

  public Optional<Lead> findById(UUID id) {
    return repository.findById(id);
  }

  public Optional<Lead> findByEmail(String email) {
    return repository.findByEmail(email);
  }
}
