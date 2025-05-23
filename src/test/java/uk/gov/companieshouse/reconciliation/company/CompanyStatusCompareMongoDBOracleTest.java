package uk.gov.companieshouse.reconciliation.company;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Projections;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.Collections;
import uk.gov.companieshouse.reconciliation.config.aws.S3ClientConfig;

@CamelSpringBootTest
@SpringBootTest
@DirtiesContext
@TestPropertySource(locations = "classpath:application-stubbed.properties")
@Import(S3ClientConfig.class)
public class CompanyStatusCompareMongoDBOracleTest {

    @Autowired
    private CamelContext context;

    @Produce("direct:company_status_mongo_oracle_trigger")
    private ProducerTemplate producerTemplate;

    @EndpointInject("mock:compare_results")
    private MockEndpoint mockEndpoint;

    @Test
    void testTriggerCompanyStatusComparisonBetweenMongoAndOracle() throws InterruptedException {
        mockEndpoint.expectedHeaderReceived("Src", "mock:oracle-multi");
        mockEndpoint.expectedHeaderReceived("SrcDescription", "Oracle");
        mockEndpoint.expectedHeaderReceived("MongoCacheKey", "mongoCompanyProfile");
        mockEndpoint.expectedHeaderReceived("MongoQuery", Collections.singletonList(Aggregates.project(Projections.include("_id", "data.company_name", "data.company_status"))));
        mockEndpoint.expectedHeaderReceived("MongoEndpoint", "mock:fruitBasket");
        mockEndpoint.expectedHeaderReceived("Target", "mock:mongoAggregation");
        mockEndpoint.expectedHeaderReceived("TargetDescription", "MongoDB - Company Profile");
        mockEndpoint.expectedHeaderReceived("OracleQuery", "SELECT 1 FROM DUAL");
        mockEndpoint.expectedHeaderReceived("OracleEndpoint", "mock:fruitTree");
        producerTemplate.sendBody(0);
        MockEndpoint.assertIsSatisfied(context);
    }
}
