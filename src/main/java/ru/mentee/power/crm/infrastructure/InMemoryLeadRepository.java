package ru.mentee.power.crm.infrastructure;

import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class InMemoryLeadRepository implements Repository<Lead> {
  private final List<Lead> storage = new ArrayList<>();

  @Override
  public Lead save(Lead entity) {
    if (!storage.contains(entity)) {
      storage.add(entity);
    }
    return entity;
  }

  @Override
  public void delete(UUID id) {
    storage.removeIf(lead -> lead.id().equals(id));
  }

  @Override
  public Optional<Lead> findById(UUID id) {
    return storage.stream()
        .filter(lead -> lead.id().equals(id))
        .findFirst();
  }

  @Override
  public Optional<Lead> findByEmail(String email) {
    return storage.stream()
        .filter(lead -> lead.contact().email().equals(email))
        .findFirst();
  }

  @Override
  public List<Lead> findAll() {
    return new ArrayList<>(storage);
  }
}
