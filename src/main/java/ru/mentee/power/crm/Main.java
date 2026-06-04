package ru.mentee.power.crm;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.LeadStatus;
import ru.mentee.power.crm.repository.LeadRepository;
import ru.mentee.power.crm.service.LeadService;
import ru.mentee.power.crm.servlet.LeadListServlet;

import java.io.File;

public class Main {
  public static void main(String[] args) throws Exception {

    int port = 8080;

    LeadRepository repository = new LeadRepository();
    LeadService leadService = new LeadService(repository);

    Address defaultAddress = new Address("Moscow", "Tverskaya", "125009");

    leadService.addLead("ivan@example.com", "+7-900-111-22-33", defaultAddress, "TechCorp", LeadStatus.NEW);
    leadService.addLead("maria@example.com", "+7-900-222-33-44", defaultAddress, "DesignStudio", LeadStatus.QUALIFIED);
    leadService.addLead("alex@example.com", "+7-900-333-44-55", defaultAddress, "DataFlow", LeadStatus.NEW);
    leadService.addLead("elena@example.com", "+7-900-444-55-66", defaultAddress, "CloudNet", LeadStatus.CONVERTED);
    leadService.addLead("dmitry@example.com", "+7-900-555-66-77", defaultAddress, "WebSoft", LeadStatus.QUALIFIED);

    String baseDir = new File(".").getAbsolutePath();

    Tomcat tomcat = new Tomcat();
    tomcat.setPort(port);
    tomcat.setBaseDir(baseDir);

    Context context = tomcat.addContext("", baseDir);
    context.getServletContext().setAttribute("leadService", leadService);

    tomcat.addServlet(context, "LeadListServlet", new LeadListServlet());
    context.addServletMappingDecoded("/leads", "LeadListServlet");

    tomcat.getConnector();
    tomcat.start();

    System.out.println("Tomcat started on port " + port);
    System.out.println("Open http://localhost:" + port + "/leads in browser");

    tomcat.getServer().await();
  }
}
