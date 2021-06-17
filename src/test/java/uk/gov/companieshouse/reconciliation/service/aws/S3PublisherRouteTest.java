package uk.gov.companieshouse.reconciliation.service.aws;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@CamelSpringBootTest
@SpringBootTest
@DirtiesContext
@TestPropertySource(locations = "classpath:application-stubbed.properties")
public class S3PublisherRouteTest {

    @Autowired
    private CamelContext context;

    @Produce("direct:s3-publisher")
    private ProducerTemplate producerTemplate;

    @EndpointInject("mock:s3-upload")
    private MockEndpoint s3Upload;

    @EndpointInject("mock:s3-presigner")
    private MockEndpoint s3Presigner;

    @AfterEach
    void tearDown() {
        s3Upload.reset();
        s3Presigner.reset();
    }

    @Test
    void testUploadFileToS3AndReturnPresignedUrl() throws InterruptedException {
        s3Upload.expectedBodyReceived().constant("file");
        s3Upload.expectedHeaderReceived(AWS2S3Constants.KEY, "key");
        s3Presigner.expectedHeaderReceived(AWS2S3Constants.DOWNLOAD_LINK_EXPIRATION_TIME, 300);
        s3Presigner.whenAnyExchangeReceived(exchange -> exchange.getIn().setBody("url"));
        Exchange result = producerTemplate.send(getExchange());
        assertEquals("url", result.getIn().getHeader("ResourceLinkReference"));
        MockEndpoint.assertIsSatisfied(context);
    }

    private Exchange getExchange() {
        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setBody("file");
        exchange.getIn().setHeader(AWS2S3Constants.KEY, "key");
        exchange.getIn().setHeader("Upload", "mock:s3-upload");
        exchange.getIn().setHeader("Presign", "mock:s3-presigner");
        exchange.getIn().setHeader(AWS2S3Constants.DOWNLOAD_LINK_EXPIRATION_TIME, 300);
        return exchange;
    }

}
