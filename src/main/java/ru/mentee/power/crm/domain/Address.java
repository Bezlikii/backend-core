package ru.mentee.power.crm.domain;

public record Address(String city, String street, String zip) {
  public Address {
    if (city == null || city.isBlank()) {
      throw new IllegalArgumentException("City не должен быть пустым или null.");
    }
    if (zip == null || zip.isBlank()) {
      throw new IllegalArgumentException("Zip не может быть пустым или null.");
    }
    if (street == null || street.isBlank()) {
      throw new IllegalArgumentException("Street не должен быть пустым или null.");
    }
  }
}
