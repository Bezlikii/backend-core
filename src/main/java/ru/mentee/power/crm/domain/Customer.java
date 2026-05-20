package ru.mentee.power.crm.domain;

import java.util.UUID;

public record Customer(UUID id, Contact contact, Address billingAddress, String loyaltyTier) {
  public Customer {
    if (id == null) {
      throw new IllegalArgumentException("Id не должен быть null");
    }
    if (contact == null) {
      throw new IllegalArgumentException("Contact не должен быть null");
    }
    if (billingAddress == null) {
      throw new IllegalArgumentException("Address не должен быть null");
    }
    if (!loyaltyTier.equals("BRONZE") && !loyaltyTier.equals("SILVER") && !loyaltyTier.equals("GOLD")) {
      throw new IllegalArgumentException("Не верный loyaltyTier: " + loyaltyTier);
    }
  }
}
