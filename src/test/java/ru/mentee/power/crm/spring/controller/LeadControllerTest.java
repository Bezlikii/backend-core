package ru.mentee.power.crm.spring.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

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
        .andExpect(content().string(containsString("name=\"name\"")))
        .andExpect(content().string(containsString("name=\"email\"")))
        .andExpect(content().string(containsString("name=\"phone\"")))
        .andExpect(content().string(containsString("name=\"city\"")))
        .andExpect(content().string(containsString("name=\"street\"")))
        .andExpect(content().string(containsString("name=\"zip\"")))
        .andExpect(content().string(containsString("name=\"company\"")))
        .andExpect(content().string(containsString("name=\"status\"")));
  }

  @Test
  void shouldCreateLeadAndRedirect() throws Exception {
    mockMvc.perform(post("/leads")
            .param("name", "Test User")
            .param("email", "test@example.com")
            .param("phone", "+79991234567")
            .param("city", "Moscow")
            .param("street", "Tverskaya 1")
            .param("zip", "101000")
            .param("company", "Acme")
            .param("status", "NEW"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/leads"));
  }

  @Test
  void shouldDisplayNewLeadInList() throws Exception {
    mockMvc.perform(post("/leads")
            .param("name", "New Lead")
            .param("email", "newlead@test.com")
            .param("phone", "+71112223344")
            .param("city", "SPb")
            .param("street", "Nevsky 1")
            .param("zip", "190000")
            .param("company", "TestCorp")
            .param("status", "CONTACTED"))
        .andExpect(status().is3xxRedirection());

    mockMvc.perform(get("/leads"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("newlead@test.com")));
  }

  @Test
  void shouldShowEditFormWithPrepopulatedData() throws Exception {
    mockMvc.perform(post("/leads")
            .param("name", "Edit Me")
            .param("email", "editme@test.com")
            .param("phone", "+79998887766")
            .param("city", "Kazan")
            .param("street", "Baumana 1")
            .param("zip", "420000")
            .param("company", "EditCorp")
            .param("status", "NEW"))
        .andExpect(status().is3xxRedirection());

    mockMvc.perform(get("/leads"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("editme@test.com")));

    String leadId = mockMvc.perform(get("/leads"))
        .andReturn()
        .getResponse()
        .getContentAsString();

    mockMvc.perform(get("/leads")
            .param("status", "NEW"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Edit Me")));

    mockMvc.perform(get("/leads/{id}/edit", findLeadId("editme@test.com")))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Редактирование лида")))
        .andExpect(content().string(containsString("value=\"Edit Me\"")))
        .andExpect(content().string(containsString("value=\"editme@test.com\"")));
  }

  @Test
  void shouldUpdateLeadAndRedirect() throws Exception {
    mockMvc.perform(post("/leads")
            .param("name", "Update Me")
            .param("email", "update@test.com")
            .param("phone", "+79991112233")
            .param("city", "Moscow")
            .param("street", "Arbat 1")
            .param("zip", "119000")
            .param("company", "OldCorp")
            .param("status", "NEW"))
        .andExpect(status().is3xxRedirection());

    String id = findLeadId("update@test.com");

    mockMvc.perform(post("/leads/{id}", id)
            .param("name", "Updated Name")
            .param("email", "updated@test.com")
            .param("phone", "+79993334455")
            .param("city", "SPb")
            .param("street", "Nevsky 10")
            .param("zip", "190000")
            .param("company", "NewCorp")
            .param("status", "CONTACTED"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/leads"));

    mockMvc.perform(get("/leads"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Updated Name")))
        .andExpect(content().string(containsString("updated@test.com")));
  }

  @Test
  void shouldReturn404ForNonexistentId() throws Exception {
    String fakeId = UUID.randomUUID().toString();

    mockMvc.perform(get("/leads/{id}/edit", fakeId))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldFilterLeadsBySearchParameter() throws Exception {
    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    mockMvc.perform(post("/leads")
            .param("name", "Ivan Petrov")
            .param("email", "ivan.search." + uniqueId + "@example.com")
            .param("phone", "+79991111111")
            .param("city", "Moscow")
            .param("street", "Lenina 1")
            .param("zip", "101000")
            .param("company", "TestCorp")
            .param("status", "NEW"))
        .andExpect(status().is3xxRedirection());

    mockMvc.perform(post("/leads")
            .param("name", "John Smith")
            .param("email", "john.search." + uniqueId + "@example.com")
            .param("phone", "+79992222222")
            .param("city", "SPb")
            .param("street", "Nevsky 1")
            .param("zip", "190000")
            .param("company", "OtherCorp")
            .param("status", "CONTACTED"))
        .andExpect(status().is3xxRedirection());

    mockMvc.perform(get("/leads").param("search", "ivan"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Ivan Petrov")))
        .andExpect(content().string(org.hamcrest.Matchers.not(containsString("John Smith"))));
  }

  @Test
  void shouldFilterLeadsByStatusParameter() throws Exception {
    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    mockMvc.perform(post("/leads")
            .param("name", "New Lead")
            .param("email", "new.status." + uniqueId + "@example.com")
            .param("phone", "+79993333333")
            .param("city", "Moscow")
            .param("street", "Tverskaya 1")
            .param("zip", "101000")
            .param("company", "TestCorp")
            .param("status", "NEW"))
        .andExpect(status().is3xxRedirection());

    mockMvc.perform(post("/leads")
            .param("name", "Contacted Lead")
            .param("email", "contacted.status." + uniqueId + "@example.com")
            .param("phone", "+79994444444")
            .param("city", "SPb")
            .param("street", "Nevsky 1")
            .param("zip", "190000")
            .param("company", "OtherCorp")
            .param("status", "CONTACTED"))
        .andExpect(status().is3xxRedirection());

    mockMvc.perform(get("/leads").param("status", "NEW"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("New Lead")))
        .andExpect(content().string(org.hamcrest.Matchers.not(containsString("Contacted Lead"))));
  }

  @Test
  void shouldReturnAllLeadsWhenNoParameters() throws Exception {
    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    mockMvc.perform(post("/leads")
            .param("name", "Lead One")
            .param("email", "one.all." + uniqueId + "@example.com")
            .param("phone", "+79995555555")
            .param("city", "Moscow")
            .param("street", "Arbat 1")
            .param("zip", "119000")
            .param("company", "Corp1")
            .param("status", "NEW"))
        .andExpect(status().is3xxRedirection());

    mockMvc.perform(post("/leads")
            .param("name", "Lead Two")
            .param("email", "two.all." + uniqueId + "@example.com")
            .param("phone", "+79996666666")
            .param("city", "SPb")
            .param("street", "Nevsky 1")
            .param("zip", "190000")
            .param("company", "Corp2")
            .param("status", "CONTACTED"))
        .andExpect(status().is3xxRedirection());

    mockMvc.perform(get("/leads"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Lead One")))
        .andExpect(content().string(containsString("Lead Two")));
  }

  @Test
  void shouldCombineSearchAndStatusFilters() throws Exception {
    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    mockMvc.perform(post("/leads")
            .param("name", "Ivan New")
            .param("email", "ivan.new." + uniqueId + "@example.com")
            .param("phone", "+79997777777")
            .param("city", "Moscow")
            .param("street", "Lenina 1")
            .param("zip", "101000")
            .param("company", "TestCorp")
            .param("status", "NEW"))
        .andExpect(status().is3xxRedirection());

    mockMvc.perform(post("/leads")
            .param("name", "Ivan Contacted")
            .param("email", "ivan.contacted." + uniqueId + "@example.com")
            .param("phone", "+79998888888")
            .param("city", "SPb")
            .param("street", "Nevsky 1")
            .param("zip", "190000")
            .param("company", "OtherCorp")
            .param("status", "CONTACTED"))
        .andExpect(status().is3xxRedirection());

    mockMvc.perform(get("/leads")
            .param("search", "ivan")
            .param("status", "NEW"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Ivan New")))
        .andExpect(content().string(org.hamcrest.Matchers.not(containsString("Ivan Contacted"))));
  }

  private String findLeadId(String email) throws Exception {
    String content = mockMvc.perform(get("/leads"))
        .andReturn()
        .getResponse()
        .getContentAsString();

    int emailIndex = content.indexOf(email);
    int tableStart = content.lastIndexOf("<tr", emailIndex);
    int editLinkStart = content.indexOf("/leads/", tableStart);
    int editLinkEnd = content.indexOf("/edit", editLinkStart);

    return content.substring(editLinkStart + "/leads/".length(), editLinkEnd);
  }
}
