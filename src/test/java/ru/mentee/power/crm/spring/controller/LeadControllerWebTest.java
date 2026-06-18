package ru.mentee.power.crm.spring.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.spring.service.LeadService;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(LeadController.class)
class LeadControllerWebTest {

  @Autowired
  MockMvc mockMvc;

  @MockitoBean
  LeadService leadService;

  @Test
  void shouldShowEditFormWithPrepopulatedData() throws Exception {
    UUID id = UUID.randomUUID();
    Address address = new Address("Moscow", "Tverskaya", "101000");
    Contact contact = new Contact("Ivan", "ivan@test.com", "+79991234567", address);
    Lead lead = new Lead(id, contact, "TestCorp", LeadStatus.NEW);

    when(leadService.findById(id)).thenReturn(Optional.of(lead));

    mockMvc.perform(get("/leads/{id}/edit", id))
        .andExpect(status().isOk())
        .andExpect(view().name("leads/edit"))
        .andExpect(model().attributeExists("lead"))
        .andExpect(model().attribute("lead", lead));
  }

  @Test
  void shouldReturn404WhenLeadNotFound() throws Exception {
    UUID fakeId = UUID.randomUUID();

    when(leadService.findById(fakeId)).thenReturn(Optional.empty());

    mockMvc.perform(get("/leads/{id}/edit", fakeId))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldUpdateLeadAndRedirect() throws Exception {
    UUID id = UUID.randomUUID();
    Address address = new Address("Moscow", "Tverskaya", "101000");
    Contact contact = new Contact("Ivan", "ivan@test.com", "+79991234567", address);
    Lead updatedLead = new Lead(id, contact, "NewCorp", LeadStatus.CONTACTED);

    when(leadService.updateLead(
        eq(id),
        any(), any(), any(), any(), any(), any()
    )).thenReturn(updatedLead);

    mockMvc.perform(post("/leads/{id}", id)
            .param("name", "Ivan")
            .param("email", "ivan@test.com")
            .param("phone", "+79991234567")
            .param("city", "Moscow")
            .param("street", "Tverskaya")
            .param("zip", "101000")
            .param("company", "NewCorp")
            .param("status", "CONTACTED"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/leads"));

    verify(leadService).updateLead(
        eq(id),
        eq("Ivan"),
        eq("ivan@test.com"),
        eq("+79991234567"),
        any(Address.class),
        eq("NewCorp"),
        eq(LeadStatus.CONTACTED)
    );
  }

  @Test
  void shouldDeleteLeadAndRedirectWhenPost() throws Exception {
    UUID id = UUID.randomUUID();

    mockMvc.perform(post("/leads/{id}/delete", id))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/leads"));

    verify(leadService).delete(id);
  }

  @Test
  void shouldReturn404WhenLeadDoesNotExist() throws Exception {
    UUID fakeId = UUID.randomUUID();

    doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND))
        .when(leadService).delete(fakeId);

    mockMvc.perform(post("/leads/{id}/delete", fakeId))
        .andExpect(status().isNotFound());

    verify(leadService).delete(fakeId);
  }
}
