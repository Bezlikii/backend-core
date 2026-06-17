package ru.mentee.power.crm.spring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.spring.dto.LeadCreateDto;
import ru.mentee.power.crm.spring.dto.LeadUpdateDto;
import ru.mentee.power.crm.spring.service.LeadService;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class LeadController {
  private final LeadService leadService;

  @GetMapping("/")
  @ResponseBody
  public String home() {
    return "Spring Boot CRM is running! Beans created: " + leadService.findAll().size() + " leads.";
  }

  @GetMapping("/leads/new")
  public String showCreateForm() {
    return "leads/create";
  }

  @PostMapping("/leads")
  public String createLead(@ModelAttribute LeadCreateDto dto) {
    Address address = new Address(dto.city(), dto.street(), dto.zip());
    leadService.addLead(dto.name(), dto.email(), dto.phone(), address, dto.company(), dto.status());
    return "redirect:/leads";
  }

  @GetMapping("/leads")
  public String showLeads(
      @RequestParam(required = false) LeadStatus status,
      Model model) {
    List<Lead> leads;
    if (status == null) {
      leads = leadService.findAll();
    } else {
      leads = leadService.findByStatus(status);
    }
    model.addAttribute("leads", leads);
    model.addAttribute("currentFilter", status);
    return "leads/list";
  }

  @GetMapping("/leads/{id}/edit")
  public String showEditForm(@PathVariable UUID id, Model model) {
    Lead lead = leadService.findById(id).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead not found"));
    model.addAttribute("lead", lead);
    return "leads/edit";
  }

  @PostMapping("/leads/{id}")
  public String updateLead(@PathVariable UUID id, @ModelAttribute LeadUpdateDto dto) {
    Address address = new Address(dto.city(), dto.street(), dto.zip());
    leadService.updateLead(id, dto.name(), dto.email(), dto.phone(), address, dto.company(), dto.status());
    return "redirect:/leads";
  }
}
