package ru.mentee.power.crm.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class CustomerTest {

  @Test
  void shouldReuseContactWhenCreatingCustomer() {
    Address addressForContact = new Address("Moscow", "Izmailovskaya", "765349");
    Address addressForBilling = new Address("Voronesh", "Lenina", "986345");
    Contact contact = new Contact("test@gmail.com", "+79167654538", addressForContact);
    Customer customer = new Customer(
        UUID.randomUUID(), contact, addressForBilling, LoyaltyTier.SILVER);
    assertThat(customer.contact().address()).isNotEqualTo(customer.billingAddress());
  }

  @Test
  void shouldDemonstrateContactReuseAcrossLeadAndCustomer() {
    Address address = new Address("Voronesh", "Lenina", "654382");
    Contact contact = new Contact("test@gmail.com", "+79165473485", address);
    Lead lead = new Lead(UUID.randomUUID(), contact, "TestCompany", LeadStatus.NEW, LeadIndustry.IT);
    Customer customer = new Customer(UUID.randomUUID(), contact, address, LoyaltyTier.SILVER);
    assertThat(lead.contact()).isEqualTo(customer.contact());
  }

  @Test
  void shouldThrowExceptionWhenIdIsNull() {
    Address billingAddress = new Address("Voronesh", "Lenina", "654382");
    Contact contact = new Contact("test@gmail.com", "+79165473485", billingAddress);
    assertThatThrownBy(() -> new Customer(null, contact, billingAddress, LoyaltyTier.GOLD))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Id");
  }

  @Test
  void shouldThrowExceptionWhenContactIsNull() {
    Address billingAddress = new Address("Voronesh", "Lenina", "654382");
    assertThatThrownBy(
        () -> new Customer(UUID.randomUUID(), null, billingAddress, LoyaltyTier.GOLD))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Contact");
  }

  @Test
  void shouldThrowExceptionWhenBillingAddressIsNull() {
    Address contactAddress = new Address("Moscow", "Lenina", "111111");
    Contact contact = new Contact("test@gmail.com", "+79165473485", contactAddress);
    assertThatThrownBy(() -> new Customer(UUID.randomUUID(), contact, null, LoyaltyTier.GOLD))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Address");
  }

  @Test
  void shouldThrowExceptionWhenLoyaltyTierIsNull() {
    Address billingAddress = new Address("Voronesh", "Lenina", "654382");
    Contact contact = new Contact("test@gmail.com", "+79165473485", billingAddress);
    assertThatThrownBy(() -> new Customer(UUID.randomUUID(), contact, billingAddress, null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("LoyaltyTier");
  }

  @Test
  void shouldCreateCustomerWhenLoyaltyTierIsBronze() {
    Address billingAddress = new Address("Moscow", "Lenina", "111111");
    Contact contact = new Contact("test@gmail.com", "+79165473485", billingAddress);
    Customer customer = new Customer(
        UUID.randomUUID(), contact, billingAddress, LoyaltyTier.BRONZE);

    assertThat(customer.loyaltyTier()).isEqualTo(LoyaltyTier.BRONZE);
  }

  @Test
  void shouldCreateCustomerWhenLoyaltyTierIsGold() {
    Address billingAddress = new Address("Moscow", "Lenina", "111111");
    Contact contact = new Contact("test@gmail.com", "+79165473485", billingAddress);
    Customer customer = new Customer(
        UUID.randomUUID(), contact, billingAddress, LoyaltyTier.GOLD);

    assertThat(customer.loyaltyTier()).isEqualTo(LoyaltyTier.GOLD);
  }
}
