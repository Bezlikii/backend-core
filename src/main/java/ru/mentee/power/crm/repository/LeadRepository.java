package ru.mentee.power.crm.repository;

import org.springframework.stereotype.Repository;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.CrudRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class LeadRepository implements CrudRepository<Lead> {
  private final Map<UUID, Lead> storage = new HashMap<>();
  private final Map<String, UUID> emailIndex = new HashMap<>();

  @Override
  public Lead save(Lead lead) {
    storage.put(lead.id(), lead);
    emailIndex.put(lead.contact().email(), lead.id());
    return lead;
  }

  @Override
  public void delete(UUID id) {
    Lead lead = storage.remove(id);
    if (lead != null) {
      emailIndex.remove(lead.contact().email());
    }
  }

  @Override
  public Optional<Lead> findById(UUID id) {
    return Optional.ofNullable(storage.get(id));
  }

  @Override
  public Optional<Lead> findByEmail(String email) {
    UUID id = emailIndex.get(email);
    return Optional.ofNullable(storage.get(id));
  }

  @Override
  public List<Lead> findAll() {
    return new ArrayList<>(storage.values());
  }
}
