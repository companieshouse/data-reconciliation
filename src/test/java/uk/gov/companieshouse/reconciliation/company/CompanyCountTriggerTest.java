package uk.gov.companieshouse.reconciliation.company;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

@CamelSpringBootTest
@SpringBootTest
@DirtiesContext
@TestPropertySource(locations = "classpath:application-stubbed.properties")
public class CompanyCountTriggerTest {

    @Autowired
    private CamelContext context;

    @EndpointInject("mock:compare_count")
    private MockEndpoint compareCount;

    @Produce("direct:company_count_trigger")
    private ProducerTemplate producerTemplate;

    @Test
    void testCreateMessage() throws InterruptedException {
        compareCount.expectedHeaderReceived("Src", "mock:corporate_body_count");
        compareCount.expectedHeaderReceived("SrcName", "Oracle");
        compareCount.expectedHeaderReceived("Target", "mock:company_profile_count");
        compareCount.expectedHeaderReceived("TargetName", "MongoDB");
        compareCount.expectedHeaderReceived("Comparison", "company profiles");
        compareCount.expectedHeaderReceived("Destination", "mock:result");
        compareCount.expectedBodyReceived().body().isEqualTo("SELECT 1 FROM DUAL");
        producerTemplate.sendBody(0);
        MockEndpoint.assertIsSatisfied(context);
    }


}
