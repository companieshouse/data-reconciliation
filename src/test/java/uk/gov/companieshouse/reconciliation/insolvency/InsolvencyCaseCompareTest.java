package uk.gov.companieshouse.reconciliation.insolvency;

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
@TestPropertySource("classpath:application-stubbed.properties")
public class InsolvencyCaseCompareTest {

    @Autowired
    private CamelContext context;

    @Produce("direct:insolvency_case_trigger")
    private ProducerTemplate producerTemplate;

    @EndpointInject("mock:compare_results")
    private MockEndpoint mockEndpoint;

    @Test
    void testTriggerCompanyStatusComparisonBetweenMongoAndOracle() throws InterruptedException {
        mockEndpoint.expectedHeaderReceived("Src", "direct:oracle-collection");
        mockEndpoint.expectedHeaderReceived("SrcDescription", "Oracle");
        mockEndpoint.expectedHeaderReceived("OracleQuery", "SELECT '12345678', 42 FROM DUAL");
        mockEndpoint.expectedHeaderReceived("OracleEndpoint", "mock:fruitTree");
        mockEndpoint.expectedHeaderReceived("OracleTransformer", "direct:oracle-insolvency-cases");
        mockEndpoint.expectedHeaderReceived("Target", "mock:mongoCompanyProfileCollection");
        mockEndpoint.expectedHeaderReceived("TargetDescription", "MongoDB - Company Insolvency");
        mockEndpoint.expectedHeaderReceived("MongoCacheKey", "mongoInsolvencyCases");
        mockEndpoint.expectedHeaderReceived("MongoEndpoint", "mock:insolvency_cases");
        mockEndpoint.expectedHeaderReceived("MongoTransformer", "direct:mongo-insolvency_cases-transformer");
        producerTemplate.sendBody(0);
        MockEndpoint.assertIsSatisfied(context);
    }
}
