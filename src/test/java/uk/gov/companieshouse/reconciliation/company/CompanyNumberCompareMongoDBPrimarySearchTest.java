package uk.gov.companieshouse.reconciliation.company;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import uk.gov.companieshouse.reconciliation.config.aws.S3ClientConfig;

@CamelSpringBootTest
@SpringBootTest
@DirtiesContext
@TestPropertySource(locations = "classpath:application-stubbed.properties")
@Import(S3ClientConfig.class)
public class CompanyNumberCompareMongoDBPrimarySearchTest {

    @Autowired
    private CamelContext context;

    @EndpointInject("mock:compare_collection")
    private MockEndpoint compareCollection;

    @Produce("direct:company_collection_mongo_primary_trigger")
    private ProducerTemplate producerTemplate;

    @AfterEach
    void after() {
        compareCollection.reset();
    }

    @Test
    void testCorrectHeadersHasBeenSet() throws InterruptedException {
        compareCollection.expectedHeaderReceived("SrcDescription", "MongoDB");
        compareCollection.expectedHeaderReceived("Src", "direct:mongo-company_number-mapper");
        compareCollection.expectedHeaderReceived("ElasticsearchEndpoint", "mock:elasticsearch-stub");
        compareCollection.expectedHeaderReceived("ElasticsearchQuery", "test");
        compareCollection.expectedHeaderReceived("TargetDescription", "Primary Index");
        compareCollection.expectedHeaderReceived("ElasticsearchLogIndices", "100000");
        compareCollection.expectedHeaderReceived("Target", "direct:elasticsearch-company_number-mapper");
        compareCollection.expectedHeaderReceived("Destination", "mock:result");
        compareCollection.expectedHeaderReceived("RecordType", "Company Number");
        compareCollection.expectedHeaderReceived("Upload", "mock:s3_bucket_destination");
        compareCollection.expectedHeaderReceived("Presign", "mock:s3_download_link");
        compareCollection.expectedHeaderReceived(AWS2S3Constants.DOWNLOAD_LINK_EXPIRATION_TIME, 2000L);
        producerTemplate.sendBody(0);
        MockEndpoint.assertIsSatisfied(context);
    }

}
