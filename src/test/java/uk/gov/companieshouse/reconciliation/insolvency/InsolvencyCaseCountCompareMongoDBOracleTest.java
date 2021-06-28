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
public class InsolvencyCaseCountCompareMongoDBOracleTest {

    @Autowired
    private CamelContext context;

    @Produce("direct:insolvency_case_count_mongo_oracle_trigger")
    private ProducerTemplate producerTemplate;

    @EndpointInject("mock:compare_results")
    private MockEndpoint mockEndpoint;

    @Test
    void testTriggerCompanyStatusComparisonBetweenMongoAndOracle() throws InterruptedException {
        mockEndpoint.expectedHeaderReceived("Src", "mock:mongoCompanyProfileCollection");
        mockEndpoint.expectedHeaderReceived("SrcDescription", "MongoDB - Number of cases");
        mockEndpoint.expectedHeaderReceived("MongoCacheKey", "mongoInsolvencyCases");
        mockEndpoint.expectedHeaderReceived("MongoEndpoint", "mock:insolvency_cases");
        mockEndpoint.expectedHeaderReceived("MongoTransformer", "direct:mongo-insolvency_cases-transformer");
        mockEndpoint.expectedHeaderReceived("Target", "direct:oracle-collection");
        mockEndpoint.expectedHeaderReceived("TargetDescription", "Oracle DB - Number of cases");
        mockEndpoint.expectedHeaderReceived("OracleQuery", "SELECT '12345678', 42 FROM DUAL");
        mockEndpoint.expectedHeaderReceived("OracleEndpoint", "mock:fruitTree");
        mockEndpoint.expectedHeaderReceived("OracleTransformer", "direct:oracle-insolvency-cases");
        producerTemplate.sendBody(0);
        MockEndpoint.assertIsSatisfied(context);
    }
}
