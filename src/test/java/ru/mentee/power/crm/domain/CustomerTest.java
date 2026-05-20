package ru.mentee.power.crm.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomerTest {

  @Test
  void shouldReuseContactWhenCreatingCustomer() {
    Address addressForContact = new Address("Moscow", "Izmailovskaya", "765349");
    Address addressForBilling = new Address("Voronesh", "Lenina", "986345");
    Contact contact = new Contact("test@gmail.com", "+79167654538", addressForContact);
    Customer customer = new Customer(UUID.randomUUID(), contact, addressForBilling, "SILVER");
    assertThat(customer.contact().address()).isNotEqualTo(customer.billingAddress());
  }

  @Test
  void shouldDemonstrateContactReuseAcrossLeadAndCustomer() {
    Address address = new Address("Voronesh", "Lenina", "654382");
    Contact contact = new Contact("test@gmail.com", "+79165473485", address);
    Lead lead = new Lead(UUID.randomUUID(), contact, "TestCompany", "NEW");
    Customer customer = new Customer(UUID.randomUUID(), contact, address, "SILVER");
    assertThat(lead.contact()).isEqualTo(customer.contact());
  }

  @Test
  void shouldThrowExceptionWhenIdIsNull() {
    Address billingAddress = new Address("Voronesh", "Lenina", "654382");
    Contact contact = new Contact("test@gmail.com", "+79165473485", billingAddress);
    assertThatThrownBy(() -> new Customer(null, contact, billingAddress, "GOLD"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Id");
  }

  @Test
  void shouldThrowExceptionWhenContactIsNull() {
    Address billingAddress = new Address("Voronesh", "Lenina", "654382");
    assertThatThrownBy(() -> new Customer(UUID.randomUUID(), null, billingAddress, "GOLD"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Contact");
  }

  @Test
  void shouldThrowExceptionWhenBillingAddressIsNull() {
    Address contactAddress = new Address("Moscow", "Lenina", "111111");
    Contact contact = new Contact("test@gmail.com", "+79165473485", contactAddress);
    assertThatThrownBy(() -> new Customer(UUID.randomUUID(), contact, null, "GOLD"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Address");
  }

  @Test
  void shouldThrowExceptionWhenInvalidLoyaltyTier() {
    Address billingAddress = new Address("Voronesh", "Lenina", "654382");
    Contact contact = new Contact("test@gmail.com", "+79165473485", billingAddress);
    assertThatThrownBy(() -> new Customer(UUID.randomUUID(), contact, billingAddress, "PLATINUM"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("loyaltyTier");
  }

  @Test
  void shouldCreateCustomerWhenLoyaltyTierIsBronze() {
    Address billingAddress = new Address("Moscow", "Lenina", "111111");
    Contact contact = new Contact("test@gmail.com", "+79165473485", billingAddress);
    Customer customer = new Customer(UUID.randomUUID(), contact, billingAddress, "BRONZE");

    assertThat(customer.loyaltyTier()).isEqualTo("BRONZE");
  }

  @Test
  void shouldCreateCustomerWhenLoyaltyTierIsGold() {
    Address billingAddress = new Address("Moscow", "Lenina", "111111");
    Contact contact = new Contact("test@gmail.com", "+79165473485", billingAddress);
    Customer customer = new Customer(UUID.randomUUID(), contact, billingAddress, "GOLD");

    assertThat(customer.loyaltyTier()).isEqualTo("GOLD");
  }
}
