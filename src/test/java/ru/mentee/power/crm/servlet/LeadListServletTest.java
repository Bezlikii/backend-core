package ru.mentee.power.crm.servlet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.service.LeadService;

@ExtendWith(MockitoExtension.class)
class LeadListServletTest {

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private ServletContext servletContext;

  @Mock
  private LeadService leadService;

  private LeadListServlet servlet;
  private StringWriter stringWriter;
  private PrintWriter printWriter;

  @BeforeEach
  void setUp() throws Exception {
    servlet = spy(new LeadListServlet());
    stringWriter = new StringWriter();
    printWriter = new PrintWriter(stringWriter);

    when(response.getWriter()).thenReturn(printWriter);
    doReturn(servletContext).when(servlet).getServletContext();
    when(servletContext.getAttribute("leadService")).thenReturn(leadService);
  }

  @Test
  void shouldSetContentTypeToHtmlWhenDoGetCalled() throws Exception {
    when(leadService.findAll()).thenReturn(Collections.emptyList());

    servlet.doGet(request, response);

    verify(response).setContentType("text/html; charset=UTF-8");
  }

  @Test
  void shouldReturnHtmlTableWhenDoGetCalled() throws Exception {
    List<Lead> leads = Arrays.asList(
        createTestLead("test@example.com", "+79161234567", "Company", LeadStatus.NEW)
    );
    when(leadService.findAll()).thenReturn(leads);

    servlet.doGet(request, response);
    printWriter.flush();
    String html = stringWriter.toString();

    verify(leadService, times(1)).findAll();
    assertThat(html)
        .contains("<table")
        .contains("<th>Email</th>")
        .contains("test@example.com");
  }

  @Test
  void shouldCallFindAllOnLeadService() throws Exception {
    when(leadService.findAll()).thenReturn(Collections.emptyList());

    servlet.doGet(request, response);

    verify(leadService, times(1)).findAll();
  }

  @Test
  void shouldGetLeadServiceFromServletContext() throws Exception {
    when(leadService.findAll()).thenReturn(Collections.emptyList());

    servlet.doGet(request, response);

    verify(servletContext).getAttribute("leadService");
  }

  @Test
  void shouldGenerateTableHeaders() throws Exception {
    when(leadService.findAll()).thenReturn(Collections.emptyList());

    servlet.doGet(request, response);
    printWriter.flush();
    String html = stringWriter.toString();

    assertThat(html)
        .contains("<th>Email</th>")
        .contains("<th>Company</th>")
        .contains("<th>Status</th>");
  }

  @Test
  void shouldGenerateTableRowForEachLead() throws Exception {
    List<Lead> leads = Arrays.asList(
        createTestLead("test1@example.com", "+79161111111", "Company1", LeadStatus.NEW),
        createTestLead("test2@example.com", "+79162222222", "Company2", LeadStatus.QUALIFIED),
        createTestLead("test3@example.com", "+79163333333", "Company3", LeadStatus.CONVERTED)
    );
    when(leadService.findAll()).thenReturn(leads);

    servlet.doGet(request, response);
    printWriter.flush();
    String html = stringWriter.toString();

    long tableRowCount = html.lines()
        .filter(line -> line.trim().startsWith("<tr>"))
        .count();

    assertThat(tableRowCount).isEqualTo(4);
  }

  @Test
  void shouldContainLeadDataInHtml() throws Exception {
    Lead lead = createTestLead(
        "john@example.com", "+79161234567", "TechCorp", LeadStatus.QUALIFIED);
    when(leadService.findAll()).thenReturn(Collections.singletonList(lead));

    servlet.doGet(request, response);
    printWriter.flush();
    String html = stringWriter.toString();

    assertThat(html)
        .contains("john@example.com")
        .contains("TechCorp")
        .contains("QUALIFIED");
  }

  @Test
  void shouldGenerateEmptyTableWhenNoLeads() throws Exception {
    when(leadService.findAll()).thenReturn(Collections.emptyList());

    servlet.doGet(request, response);
    printWriter.flush();
    String html = stringWriter.toString();

    assertThat(html)
        .contains("<thead>")
        .contains("<tbody>")
        .doesNotContain("<td>");
  }

  private Lead createTestLead(String email, String phone, String company, LeadStatus status) {
    Address address = new Address("Moscow", "Test Street", "123456");
    Contact contact = new Contact(email, phone, address);
    return new Lead(UUID.randomUUID(), contact, company, status);
  }
}
