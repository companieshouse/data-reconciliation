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
public class CompanyCountMongoDBOracleTriggerTest {

    @Autowired
    private CamelContext context;

    @EndpointInject("mock:compare_count")
    private MockEndpoint compareCount;

    @Produce("direct:company_count_trigger")
    private ProducerTemplate producerTemplate;

    @AfterEach
    void after() {
        compareCount.reset();
    }

    @Test
    void testCreateMessage() throws InterruptedException {
        compareCount.expectedHeaderReceived("Src", "mock:corporate_body_count");
        compareCount.expectedHeaderReceived("SrcName", "Oracle");
        compareCount.expectedHeaderReceived("Target", "mock:company_profile_count");
        compareCount.expectedHeaderReceived("TargetName", "MongoDB");
        compareCount.expectedHeaderReceived("Upload", "mock:s3_bucket_destination");
        compareCount.expectedHeaderReceived("Presign", "mock:s3_download_link");
        compareCount.expectedHeaderReceived(AWS2S3Constants.DOWNLOAD_LINK_EXPIRATION_TIME, 2000L);
        compareCount.expectedBodyReceived().body().isEqualTo("SELECT 1 FROM DUAL");
        producerTemplate.sendBody(0);
        MockEndpoint.assertIsSatisfied(context);
    }
}
