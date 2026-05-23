package ru.mentee.power.crm.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.model.Lead;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LeadRepositoryTest {
  private LeadRepository repository;

  @BeforeEach
  void setUp() {
    repository = new LeadRepository();
  }

  @Test
  void shouldSaveAndFindLeadByIdWhenLeadSaved() {
    Lead lead = new Lead("lead1", "test@gmail.com", "+79167654382","TestCompany", "NEW");
    repository.save(lead);
    assertThat(repository.findById("lead1")).isEqualTo(lead);
  }

  @Test
  void shouldReturnNullWhenLeadNotFound() {
    assertThat(repository.findById("lead1")).isNull();
  }

  @Test
  void shouldReturnAllLeadsWhenMultipleLeadsSaved() {
    Lead lead1 = new Lead("lead1", "test@gmail.com", "+79167654382","TestCompany", "NEW");
    Lead lead2 = new Lead("lead2", "test@gmail.com", "+79167654382","TestCompany", "NEW");
    Lead lead3 = new Lead("lead3", "test@gmail.com", "+79167654382","TestCompany", "NEW");
    repository.save(lead1);
    repository.save(lead2);
    repository.save(lead3);
    assertThat(repository.findAll()).hasSize(3);
  }

  @Test
  void shouldDeleteLeadWhenLeadExists() {
    Lead lead = new Lead("lead1", "test@gmail.com", "+79167654382","TestCompany", "NEW");
    repository.save(lead);
    repository.delete("lead1");
    assertThat(repository.findById("lead1")).isNull();
    assertThat(repository.findAll()).isEmpty();
  }

  @Test
  void shouldOverwriteLeadWhenSaveWithSameId() {
    Lead lead1 = new Lead("lead1", "test@gmail.com", "+79167654382","TestCompany", "NEW");
    repository.save(lead1);
    Lead lead2 = new Lead("lead1", "test2@gmail.com", "+79167654382","TestCompany", "NEW");
    repository.save(lead2);
    assertThat(repository.findById("lead1")).isEqualTo(lead2);
  }

  @Test
  void shouldFindFasterWithMapThanWithListFilter() {
    List<Lead> leadList = new ArrayList<>();
    for (int i = 0; i < 1000; i++) {
      String id = "lead-" + i;
      Lead lead = new Lead(id, "email" + i + "@test.com", "+7" + i, "Company" + i, "NEW");
      repository.save(lead);
      leadList.add(lead);
    }

    String targetId = "lead-500";

    long mapStart = System.nanoTime();
    Lead foundInMap = repository.findById(targetId);
    long mapDuration = System.nanoTime() - mapStart;

    long listStart = System.nanoTime();
    Lead foundInList = leadList.stream()
        .filter(lead -> lead.id().equals(targetId))
        .findFirst()
        .orElse(null);
    long listDuration = System.nanoTime() - listStart;

    assertThat(foundInMap).isEqualTo(foundInList);
    assertThat(listDuration).isGreaterThan(mapDuration * 10);

    System.out.println("Map поиск: " + mapDuration + " ns");
    System.out.println("List поиск: " + listDuration + " ns");
    System.out.println("Ускорение: " + (listDuration / mapDuration) + "x");
  }


}
