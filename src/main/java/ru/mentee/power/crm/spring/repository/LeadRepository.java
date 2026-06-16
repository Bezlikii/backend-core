package ru.mentee.power.crm.spring.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;
import ru.mentee.power.crm.domain.Lead;

@Repository
public class LeadRepository {
  private final Map<UUID, Lead> storage = new HashMap<>();
  private final Map<String, UUID> emailIndex = new HashMap<>();

  public Lead save(Lead lead) {
    storage.put(lead.id(), lead);
    emailIndex.put(lead.contact().email(), lead.id());
    return lead;
  }

  public void delete(UUID id) {
    Lead lead = storage.remove(id);
    if (lead != null) {
      emailIndex.remove(lead.contact().email());
    }
  }

  public Optional<Lead> findById(UUID id) {
    return Optional.ofNullable(storage.get(id));
  }

  public Optional<Lead> findByEmail(String email) {
    UUID id = emailIndex.get(email);
    return Optional.ofNullable(storage.get(id));
  }

  public List<Lead> findAll() {
    return new ArrayList<>(storage.values());
  }
}
