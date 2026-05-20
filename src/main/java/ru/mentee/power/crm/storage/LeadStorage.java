package ru.mentee.power.crm.storage;

import ru.mentee.power.crm.domain.Lead;

import java.util.UUID;

public class LeadStorage {
  private Lead[] leads = new Lead[100];

  public boolean add(Lead lead) {
    for (int i = 0; i < leads.length; i++) {
      if (leads[i] != null && leads[i].contact().email().equals(lead.contact().email())) {
        return false;
      }
    }

    for (int i = 0; i < leads.length; i++) {
      if (leads[i] == null) {
        leads[i] = lead;
        return true;
      }
    }

    throw new IllegalStateException("Storage is full");
  }

  public Lead[] findAll() {
    int count = size();

    Lead[] result = new Lead[count];

    int resultIndex = 0;
    for (int i = 0; i < leads.length; i++) {
      if (leads[i] != null) {
        result[resultIndex++] = leads[i];
      }
    }

    return result;
  }

  public int size() {
    int count = 0;
    for (Lead lead : leads) {
      if (lead != null) {
        count++;
      }
    }
    return count;
  }

  public Lead findById(UUID id) {
    for (Lead lead : leads) {
      if (lead != null && lead.id().equals(id)) {
        return lead;
      }
    }
    return null;
  }
}
