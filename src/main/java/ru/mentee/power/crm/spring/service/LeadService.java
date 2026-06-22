package ru.mentee.power.crm.spring.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.spring.repository.LeadRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class LeadService {
  private static final Logger LOG = LoggerFactory.getLogger(LeadService.class);
  private final LeadRepository repository;

  public LeadService(LeadRepository repository) {
    this.repository = repository;
    LOG.info("LeadService constructor called");
  }

  @PostConstruct
  void init() {
    LOG.info("LeadService @PostConstruct init() called — Bean lifecycle phase");
  }

  public Lead addLead(String name, String email, String phone, Address address,
      String company, LeadStatus status) {
    if (repository.findByEmail(email).isPresent()) {
      throw new IllegalStateException("Lead with email already exists: " + email);
    }

    Contact contact = new Contact(name, email, phone, address);
    Lead lead = new Lead(UUID.randomUUID(), contact, company, status);
    return repository.save(lead);
  }

  public Lead updateLead(UUID id, String name, String email, String phone, Address address,
                         String company, LeadStatus status) {
    if (repository.findById(id).isPresent()) {
      Contact contact = new Contact(name, email, phone, address);
      Lead lead = new Lead(id, contact, company, status);
      return repository.save(lead);
    } else {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead with id " + id + " not found");
    }
  }

  public List<Lead> findAll() {
    return repository.findAll();
  }

  public List<Lead> findByStatus(LeadStatus status) {
    return repository.findAll().stream()
        .filter(lead -> lead.status().equals(status))
        .toList();
  }

  public Optional<Lead> findById(UUID id) {
    return repository.findById(id);
  }

  public Optional<Lead> findByEmail(String email) {
    return repository.findByEmail(email);
  }

  public void delete(UUID id) {
    repository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Lead with id " + id + " not found"));
    repository.delete(id);
  }

  public List<Lead> findLeads(String search, String status) {
    Stream<Lead> stream = repository.findAll().stream();

    if (search != null && !search.isEmpty()) {
      stream = stream.filter(lead ->
          lead.contact().name().toLowerCase().contains(search.toLowerCase())
              || lead.contact().email().toLowerCase().contains(search.toLowerCase())
      );
    }

    if (status != null && !status.isEmpty()) {
      stream = stream.filter(lead ->
          lead.status().equals(LeadStatus.valueOf(status))
      );
    }
    return stream.toList();
  }
}
