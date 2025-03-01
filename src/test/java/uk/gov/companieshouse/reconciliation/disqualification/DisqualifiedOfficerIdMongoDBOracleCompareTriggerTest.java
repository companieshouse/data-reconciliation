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
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import uk.gov.companieshouse.reconciliation.config.aws.S3ClientConfig;

@CamelSpringBootTest
@SpringBootTest
@DirtiesContext
@TestPropertySource(locations = "classpath:application-stubbed.properties")
@Import(S3ClientConfig.class)
public class DisqualifiedOfficerIdMongoDBOracleCompareTriggerTest {

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
        compareCollection.expectedHeaderReceived("OracleQuery", "SELECT '1234567890' FROM DUAL");
        compareCollection.expectedHeaderReceived("OracleEndpoint", "mock:officer_compare_src");
        compareCollection.expectedHeaderReceived("SrcDescription", "Oracle");
        compareCollection.expectedHeaderReceived("Src", "direct:oracle-collection");
        compareCollection.expectedHeaderReceived("TargetDescription", "MongoDB");
        compareCollection.expectedHeaderReceived("Target", "direct:mongo-disqualified_officer-mapper");
        compareCollection.expectedHeaderReceived("Destination", "mock:result");
        compareCollection.expectedHeaderReceived("RecordType", "Disqualified Officer");
        compareCollection.expectedHeaderReceived("Upload", "mock:s3_bucket_destination");
        compareCollection.expectedHeaderReceived("Presign", "mock:s3_download_link");
        producerTemplate.sendBody(0);
        MockEndpoint.assertIsSatisfied(context);
    }
}
