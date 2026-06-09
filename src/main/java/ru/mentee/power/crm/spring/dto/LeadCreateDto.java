package ru.mentee.power.crm.spring.dto;

import ru.mentee.power.crm.domain.LeadStatus;

public record LeadCreateDto(
    String email,
    String phone,
    String city,
    String street,
    String zip,
    String company,
    LeadStatus status
) {
}
