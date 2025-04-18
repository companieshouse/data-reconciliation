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
        compareCollection.expectedHeaderReceived("OracleQuery", "SELECT '12345678' FROM DUAL");
        compareCollection.expectedHeaderReceived("OracleEndpoint", "mock:fruitTree");
        compareCollection.expectedHeaderReceived("SrcDescription", "Oracle");
        compareCollection.expectedHeaderReceived("Src", "direct:oracle-collection");
        compareCollection.expectedHeaderReceived("TargetDescription", "MongoDB");
        compareCollection.expectedHeaderReceived("Target", "direct:mongo-company_number-mapper");
        compareCollection.expectedHeaderReceived("Destination", "mock:result");
        compareCollection.expectedHeaderReceived("RecordType", "Company Number");
        compareCollection.expectedHeaderReceived("Upload", "mock:s3_bucket_destination");
        compareCollection.expectedHeaderReceived("Presign", "mock:s3_download_link");
        compareCollection.expectedHeaderReceived(AWS2S3Constants.DOWNLOAD_LINK_EXPIRATION_TIME, 2000L);
        producerTemplate.sendBody(0);
        MockEndpoint.assertIsSatisfied(context);
    }
}
