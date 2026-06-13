package ru.mentee.power.crm.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.domain.IndustryDictionary;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadIndustry;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.repository.LeadRepository;

@Service
public class LeadService {

  private final LeadRepository repository;
  private final IndustryDictionary industryDictionary;

  public LeadService(LeadRepository repository,
                     IndustryDictionary industryDictionary) {
    this.repository = repository;
    this.industryDictionary = industryDictionary;
  }

  public Lead addLead(String email, String phone, Address address,
                      String company, LeadStatus status, LeadIndustry industry) {
    if (repository.findByEmail(email).isPresent()) {
      throw new IllegalStateException("Lead with email already exists: " + email);
    }

    Contact contact = new Contact(email, phone, address);
    Lead lead = new Lead(UUID.randomUUID(), contact, company, status, industry);
    return repository.save(lead);
  }

  public List<Lead> findAll() {
    return repository.findAll();
  }

  public List<Lead> findByStatus(LeadStatus status) {
    return repository.findAll().stream()
        .filter(lead -> lead.status().equals(status))
        .toList();
  }

  public List<Lead> findByIndustry(LeadIndustry industry) {
    return repository.findByIndustry(industry);
  }

  public Optional<Lead> findById(UUID id) {
    return repository.findById(id);
  }

  public Optional<Lead> findByEmail(String email) {
    return repository.findByEmail(email);
  }

  public List<LeadIndustry> getActiveIndustries() {
    return industryDictionary.getActiveItems();
  }
}
