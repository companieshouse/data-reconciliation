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
public class CompareNumberCompareMongoDBAlphaSearchTest {

    @Autowired
    private CamelContext context;

    @EndpointInject("mock:compare_collection")
    private MockEndpoint compareCollection;

    @Produce("direct:company_collection_mongo_alpha_trigger")
    private ProducerTemplate producerTemplate;

    @AfterEach
    void after() {
        compareCollection.reset();
    }

    @Test
    void testCorrectHeadersHasBeenSet() throws InterruptedException {
        compareCollection.expectedHeaderReceived("MongoDescription", "MongoDB");
        compareCollection.expectedHeaderReceived("MongoTargetHeader", "SrcList");
        compareCollection.expectedHeaderReceived("Src", "direct:mongo-company_number-mapper");
        compareCollection.expectedHeaderReceived("ElasticsearchEndpoint", "mock:elasticsearch-alpha-stub");
        compareCollection.expectedHeaderReceived("ElasticsearchQuery", "alpha-test");
        compareCollection.expectedHeaderReceived("ElasticsearchDescription", "Alpha Index");
        compareCollection.expectedHeaderReceived("ElasticsearchTargetHeader", "TargetList");
        compareCollection.expectedHeaderReceived("ElasticsearchLogIndices", "100000");
        compareCollection.expectedHeaderReceived("Target", "direct:elasticsearch-company_number-mapper");
        compareCollection.expectedHeaderReceived("Destination", "mock:elasticsearch");
        compareCollection.expectedHeaderReceived("RecordType", "Company Number");
        compareCollection.expectedHeaderReceived("RecordType", "Company Number");
        compareCollection.expectedHeaderReceived("Upload", "mock:s3_bucket_destination");
        compareCollection.expectedHeaderReceived("Presign", "mock:s3_download_link");
        compareCollection.expectedHeaderReceived(MongoDbConstants.DISTINCT_QUERY_FIELD, "_id");
        producerTemplate.sendBody(0);
        MockEndpoint.assertIsSatisfied(context);
    }

}
