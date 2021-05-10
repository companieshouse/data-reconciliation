package uk.gov.companieshouse.reconciliation.disqualification;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.mongodb.MongoDbConstants;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

@CamelSpringBootTest
@SpringBootTest
@DirtiesContext
@TestPropertySource(locations = "classpath:application-stubbed.properties")
public class DisqualifiedOfficerCompareTriggerTest {

    @Autowired
    private CamelContext context;

    @EndpointInject("mock:compare_collection")
    private MockEndpoint compareCollection;

    @Produce("direct:dsq_officer_trigger")
    private ProducerTemplate producerTemplate;

    @BeforeEach
    void before(){
        compareCollection.reset();
    }

    @Test
    void testCreateOfficerCompareMessage() throws InterruptedException {
        compareCollection.expectedHeaderReceived("Src", "mock:officer_compare_src");
        compareCollection.expectedHeaderReceived("SrcName", "Oracle");
        compareCollection.expectedHeaderReceived("Target", "mock:officer_compare_target");
        compareCollection.expectedHeaderReceived("TargetName", "MongoDB");
        compareCollection.expectedHeaderReceived("Destination", "mock:send_email");
        compareCollection.expectedHeaderReceived(MongoDbConstants.DISTINCT_QUERY_FIELD, "officer_id_raw");
        compareCollection.expectedBodyReceived().constant("SELECT '1234567890' FROM DUAL");
        producerTemplate.sendBody(0);
        MockEndpoint.assertIsSatisfied(context);
    }
}
