package ru.mentee.power.crm.spring.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class LeadControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Test
  void shouldReturnLeadsPage() throws Exception {
    mockMvc.perform(get("/leads"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Email")));
  }

  @Test
  void shouldShowCreateForm() throws Exception {
    mockMvc.perform(get("/leads/new"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Добавить нового лида")))
        .andExpect(content().string(containsString("name=\"email\"")))
        .andExpect(content().string(containsString("name=\"phone\"")))
        .andExpect(content().string(containsString("name=\"city\"")))
        .andExpect(content().string(containsString("name=\"street\"")))
        .andExpect(content().string(containsString("name=\"zip\"")))
        .andExpect(content().string(containsString("name=\"company\"")))
        .andExpect(content().string(containsString("name=\"status\"")))
        .andExpect(content().string(containsString("name=\"industry\"")));
  }

  @Test
  void shouldCreateLeadAndRedirect() throws Exception {
    mockMvc.perform(post("/leads")
            .param("email", "test@example.com")
            .param("phone", "+79991234567")
            .param("city", "Moscow")
            .param("street", "Tverskaya 1")
            .param("zip", "101000")
            .param("company", "Acme")
            .param("status", "NEW")
            .param("industry", "IT"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/leads"));
  }

  @Test
  void shouldDisplayNewLeadInList() throws Exception {
    mockMvc.perform(post("/leads")
            .param("email", "newlead@test.com")
            .param("phone", "+71112223344")
            .param("city", "SPb")
            .param("street", "Nevsky 1")
            .param("zip", "190000")
            .param("company", "TestCorp")
            .param("status", "CONTACTED")
            .param("industry", "FINANCE"))
        .andExpect(status().is3xxRedirection());

    mockMvc.perform(get("/leads"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("newlead@test.com")));
  }

  @Test
  void shouldFilterLeadsByIndustry() throws Exception {
    mockMvc.perform(get("/leads").param("industry", "IT"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("TechCorp")))
        .andExpect(content().string(not(containsString("DesignStudio"))));
  }

  @Test
  void shouldShowOnlyActiveIndustriesInSelect() throws Exception {
    mockMvc.perform(get("/leads"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("<option value=\"IT\"")))
        .andExpect(content().string(containsString("<option value=\"FINANCE\"")))
        .andExpect(content().string(containsString("<option value=\"RETAIL\"")));
  }

  @Test
  void shouldPreserveStatusFilterWhenIndustrySelected() throws Exception {
    mockMvc.perform(get("/leads")
            .param("status", "NEW")
            .param("industry", "IT"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("TechCorp")))
        .andExpect(content().string(not(containsString("WebSoft"))));
  }
}
