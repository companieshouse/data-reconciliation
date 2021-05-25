package uk.gov.companieshouse.reconciliation.company;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
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
public class CompanyNameCompareMongoDBPrimarySearchTest {

    @Autowired
    private CamelContext context;

    @EndpointInject("mock:compare_results")
    private MockEndpoint target;

    @Produce("direct:company_name_mongo_primary_trigger")
    private ProducerTemplate producerTemplate;

    @BeforeEach
    void setUp() {
        target.reset();
    }

    @Test
    void testSetHeadersAndProduceMessage() throws InterruptedException {
        target.expectedHeaderReceived("Src", "mock:mongoCompanyProfileCollection");
        target.expectedHeaderReceived("SrcDescription", "MongoDB - Company Profile");
        target.expectedHeaderReceived("Target", "mock:elasticsearch-collection");
        target.expectedHeaderReceived("TargetDescription", "Primary Search Index");
        target.expectedHeaderReceived("ElasticsearchCacheKey", "elasticsearchPrimary");
        target.expectedHeaderReceived("ElasticsearchEndpoint", "mock:elasticsearch-stub");
        target.expectedHeaderReceived("ElasticsearchQuery", "test");
        target.expectedHeaderReceived("RecordType", "Company Number");
        target.expectedHeaderReceived("Destination", "mock:log-result");
        producerTemplate.sendBody(0);
        MockEndpoint.assertIsSatisfied(context);
    }
}
