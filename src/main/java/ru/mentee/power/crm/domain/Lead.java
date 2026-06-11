package ru.mentee.power.crm.domain;

import java.util.Objects;
import java.util.UUID;

public record Lead(UUID id, Contact contact, String company, LeadStatus status) {
  public Lead {
    if (id == null) {
      throw new IllegalArgumentException("Id не должно быть null");
    }
    if (contact == null) {
      throw new IllegalArgumentException("Contact не должен быть null");
    }
    if (status == null) {
      throw new IllegalArgumentException("Status не может быть null");
    }
    if (company == null) {
      throw new IllegalArgumentException("Company не должно быть null");
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Lead lead = (Lead) o;
    return Objects.equals(id, lead.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
