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
public class CompanyStatusCompareMongoDBOracleTest {

    @Autowired
    private CamelContext context;

    @Produce("direct:company_status_mongo_oracle_trigger")
    private ProducerTemplate producerTemplate;

    @EndpointInject("mock:compare_results")
    private MockEndpoint mockEndpoint;

    @Test
    void testTriggerCompanyStatusComparisonBetweenMongoAndOracle() throws InterruptedException {
        mockEndpoint.expectedHeaderReceived("Src", "mock:mongoCompanyProfileCollection");
        mockEndpoint.expectedHeaderReceived("SrcDescription", "MongoDB - Company Profile");
        mockEndpoint.expectedHeaderReceived("Target", "mock:oracle-multi");
        mockEndpoint.expectedHeaderReceived("TargetDescription", "Oracle");
        mockEndpoint.expectedHeaderReceived("OracleQuery", "file:sql/company_status/");
        mockEndpoint.expectedHeaderReceived("OracleEndpoint", "mock:fruitTree");
        producerTemplate.sendBody(0);
        MockEndpoint.assertIsSatisfied(context);
    }
}
