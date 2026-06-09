package ru.mentee.power.crm.spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.service.LeadService;
import ru.mentee.power.crm.spring.dto.LeadCreateDto;

import java.util.List;

@Controller
public class LeadController {
  private final LeadService leadService;

  public LeadController(LeadService leadService) {
    this.leadService = leadService;
  }

  @GetMapping("/leads/new")
  public String showCreateForm() {
    return "leads/create";
  }

  @PostMapping("/leads")
  public String createLead(@ModelAttribute LeadCreateDto dto) {
    Address address = new Address(dto.city(), dto.street(), dto.zip());
    leadService.addLead(dto.email(), dto.phone(), address, dto.company(), dto.status());
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
}
