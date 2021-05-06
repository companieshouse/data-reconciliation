package uk.gov.companieshouse.reconciliation.company;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.aws2.ses.Ses2Constants;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@CamelSpringBootTest
@SpringBootTest
@DirtiesContext
@TestPropertySource(locations = "classpath:application-stubbed.properties")
public class CompanyEmailTriggerTest {

    @Autowired
    private CamelContext context;

    @EndpointInject("mock:send_email")
    private MockEndpoint sendEmail;

    @Produce("direct:company_email_trigger")
    private ProducerTemplate producerTemplate;

    @AfterEach
    void after() {
        sendEmail.reset();
    }

    @Test
    void testCreateCompanyEmailMessage() throws InterruptedException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String currentDate = LocalDate.now().format(formatter);

        sendEmail.expectedHeaderReceived(Ses2Constants.SUBJECT, "Company Profile Comparisons ("+currentDate+")");
        sendEmail.expectedHeaderReceived(Ses2Constants.TO, "jsmith@test.com,jdoe@test.com");
        sendEmail.expectedHeaderReceived("Destination", "mock:ses_broadcast_email");
        producerTemplate.sendBody(0);
        MockEndpoint.assertIsSatisfied(context);
    }

}

