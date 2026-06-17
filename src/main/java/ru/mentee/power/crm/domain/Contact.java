package ru.mentee.power.crm.domain;

public record Contact(String name, String email, String phone, Address address) {
  public Contact {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Name не должен быть пустой или null");
    }
    if (email == null || email.isBlank()) {
      throw new IllegalArgumentException("Email не должен быть пустой или null");
    }

    if (phone == null || phone.isBlank()) {
      throw new IllegalArgumentException("Phone не должен быть пустой или null");
    }

    if (address == null) {
      throw new IllegalArgumentException("Address не должен быть null");
    }
  }
}
