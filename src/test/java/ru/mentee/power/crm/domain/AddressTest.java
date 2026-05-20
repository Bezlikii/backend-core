package ru.mentee.power.crm.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;

class AddressTest {

  @Test
  void shouldCreateAddressWhenValidData() {
    Address address = new Address("Moscow", "Izmailovskaya", "876362");
    assertThat(address.city()).isEqualTo("Moscow");
    assertThat(address.street()).isEqualTo("Izmailovskaya");
    assertThat(address.zip()).isEqualTo("876362");
  }

  @Test
  void shouldBeEqualWhenSameData() {
    Address address1 = new Address("Moscow", "Izmailovskaya", "876362");
    Address address2 = new Address("Moscow", "Izmailovskaya", "876362");
    assertThat(address1).isEqualTo(address2);
    assertThat(address1.hashCode()).isEqualTo(address2.hashCode());
  }

  @Test
  void shouldThrowExceptionWhenCityIsNull() {
    assertThatThrownBy(() -> new Address(null, "Izmailovskaya", "876362"))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void shouldThrowExceptionWhenZipIsBlank() {
    try {
      new Address("Moscow", "Izmailovskaya", "");
      fail("Ожидает IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage()).contains("Zip");
    }
  }

  @Test
  void shouldThrowExceptionWhenCityIsBlank() {
    assertThatThrownBy(() -> new Address("  ", "Izmailovskaya", "876362"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("City");
  }

  @Test
  void shouldThrowExceptionWhenZipIsOnlySpaces() {
    assertThatThrownBy(() -> new Address("Moscow", "Izmailovskaya", "   "))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Zip");
  }

  @Test
  void shouldThrowExceptionWhenZipIsNull() {
    assertThatThrownBy(() -> new Address("Moscow", "Izmailovskaya", null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Zip");
  }
}
