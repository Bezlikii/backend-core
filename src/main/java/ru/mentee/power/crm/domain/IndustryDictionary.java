package ru.mentee.power.crm.domain;

import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Component
public class IndustryDictionary implements Dictionary<LeadIndustry> {

  private final Map<LeadIndustry, IndustryEntry> entries;

  public IndustryDictionary() {
    this.entries = Map.of(
        LeadIndustry.IT, new IndustryEntry(LeadIndustry.IT, 1, true),
        LeadIndustry.FINANCE, new IndustryEntry(LeadIndustry.FINANCE, 2, true),
        LeadIndustry.RETAIL, new IndustryEntry(LeadIndustry.RETAIL, 3, true)
    );
  }

  @Override
  public List<LeadIndustry> getActiveItems() {
    return entries.values().stream()
        .filter(IndustryEntry::active)
        .sorted(Comparator.comparingInt(IndustryEntry::sortOrder))
        .map(IndustryEntry::industry)
        .toList();
  }

  @Override
  public boolean isActive(LeadIndustry industry) {
    IndustryEntry entry = entries.get(industry);
    return entry != null && entry.active();
  }

  private record IndustryEntry(LeadIndustry industry, int sortOrder, boolean active) {
  }
}
