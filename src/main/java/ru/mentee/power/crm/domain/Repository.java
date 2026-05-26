package ru.mentee.power.crm.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface Repository<T> {
  T save(T entity);

  void delete(UUID id);

  Optional<T> findById(UUID id);

  Optional<T> findByEmail(String email);

  List<T> findAll();
}