package uk.gov.companieshouse.reconciliation.company;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.mongodb.MongoDbConstants;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

@CamelSpringBootTest
@SpringBootTest
@DirtiesContext
@TestPropertySource(locations = "classpath:application-stubbed.properties")
public class CompanyNumberCompareOracleMongoDBTriggerTest {

    @Autowired
    private CamelContext context;

    @EndpointInject("mock:compare_collection")
    private MockEndpoint compareCollection;

    @Produce("direct:company_collection_trigger")
    private ProducerTemplate producerTemplate;

    @AfterEach
    void after() {
        compareCollection.reset();
    }

    @Test
    void testCreateCompanyNumberCompareMessage() throws InterruptedException {
        compareCollection.expectedHeaderReceived("Src", "mock:fruitTree");
        compareCollection.expectedHeaderReceived("SrcName", "Oracle");
        compareCollection.expectedHeaderReceived("Target", "mock:fruitBasket");
        compareCollection.expectedHeaderReceived("TargetName", "MongoDB");
        compareCollection.expectedHeaderReceived("Destination", "mock:result");
        compareCollection.expectedBodyReceived().body().isEqualTo("SELECT '12345678' FROM DUAL");
        compareCollection.expectedHeaderReceived(MongoDbConstants.DISTINCT_QUERY_FIELD, "_id");
        producerTemplate.sendBody(0);
        MockEndpoint.assertIsSatisfied(context);
    }
}
