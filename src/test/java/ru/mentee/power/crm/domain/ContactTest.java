package ru.mentee.power.crm.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ContactTest {

  @Test
  void shouldCreateContactWhenValidData() {
    Contact contact = new Contact("Alex", "Petrov", "alex@gmail.com");
    assertThat(contact.firstName()).isEqualTo("Alex");
    assertThat(contact.lastName()).isEqualTo("Petrov");
    assertThat(contact.email()).isEqualTo("alex@gmail.com");
  }

  @Test
  void shouldBeEqualWhenSameData() {
    Contact contact = new Contact("Alex", "Petrov", "alex@gmail.com");
    Contact contact2 = new Contact("Alex", "Petrov", "alex@gmail.com");
    assertThat(contact.equals(contact2)).isTrue();
    assertThat(contact.hashCode()).isEqualTo(contact2.hashCode());
  }

  @Test
  void shouldNotBeEqualWhenDifferentData() {
    Contact contact = new Contact("Alex", "Petrov", "alex@gmail.com");
    Contact contact2 = new Contact("Elena", "Kiseleva", "elena@gmail.com");
    assertThat(contact).isNotEqualTo(contact2);
  }
}