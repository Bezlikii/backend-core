package ru.mentee.power.crm.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ContactTest {

  @Test
  void shouldCreateContactWhenValidData() {
    Address address = new Address("Moscow", "Izmailovskaya", "675438");
    Contact contact = new Contact("alex@gmail.com", "+78754673456", address);
    assertThat(contact.address()).isEqualTo(address);
    assertThat(contact.address().city()).isEqualTo("Moscow");
  }

  @Test
  void shouldDelegateToAddressWhenAccessingCity() {
    Address address = new Address("Moscow", "Izmailovskaya", "675438");
    Contact contact = new Contact("alex@gmail.com", "+78754673456", address);
    assertThat(contact.address().city()).isEqualTo("Moscow");
    assertThat(contact.address().street()).isEqualTo("Izmailovskaya");
  }

  @Test
  void shouldThrowExceptionWhenAddressIsNull() {
    assertThatThrownBy(() -> new Contact("alex@gmail.com", "+78754673456", null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Address");
  }

  @Test
  void shouldThrowExceptionWhenEmailIsNull() {
    Address address = new Address("Moscow", "Lenina", "111111");
    assertThatThrownBy(() -> new Contact(null, "+78754673456", address))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Email");
  }

  @Test
  void shouldThrowExceptionWhenPhoneIsNull() {
    Address address = new Address("Moscow", "Lenina", "111111");
    assertThatThrownBy(() -> new Contact("alex@gmail.com", null, address))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Phone");
  }

  @Test
  void shouldThrowExceptionWhenEmailIsBlank() {
    Address address = new Address("Moscow", "Lenina", "111111");
    assertThatThrownBy(() -> new Contact("   ", "+78754673456", address))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Email");
  }

  @Test
  void shouldThrowExceptionWhenPhoneIsBlank() {
    Address address = new Address("Moscow", "Lenina", "111111");
    assertThatThrownBy(() -> new Contact("alex@gmail.com", "   ", address))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Phone");
  }
}
