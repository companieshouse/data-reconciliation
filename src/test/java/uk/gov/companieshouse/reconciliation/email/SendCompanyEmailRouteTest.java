package uk.gov.companieshouse.reconciliation.email;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.builder.ExpressionBuilder;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import uk.gov.companieshouse.reconciliation.model.ResourceLinksWrapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

@CamelSpringBootTest
@SpringBootTest
@DirtiesContext
@TestPropertySource(locations = "classpath:application-stubbed.properties")
@UseAdviceWith
public class SendCompanyEmailRouteTest {

    @Autowired
    private ModelCamelContext context;

    @EndpointInject("mock:kafka-endpoint")
    private MockEndpoint kafkaEndpoint;

    @EndpointInject("mock:s3-uploader")
    private MockEndpoint s3Uploader;

    @EndpointInject("mock:s3-presigner")
    private MockEndpoint s3Presigner;

    @Produce("direct:send-company-email")
    private ProducerTemplate producerTemplate;

    @AfterEach
    void after() {
        s3Uploader.reset();
        s3Presigner.reset();
        kafkaEndpoint.reset();
    }

    @Test
    void testSendEmailAggregatesTwoMessage() throws Exception {
        AdviceWith.adviceWith(context.getRouteDefinitions().get(0), context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                interceptSendToEndpoint("mock:kafka-endpoint")
                        .process(exchange -> {
                            ResourceLinksWrapper downloadsList = exchange.getIn().getHeader("ResourceLinks", ResourceLinksWrapper.class);
                            assertEquals(2, downloadsList.getDownloadLinkList().size());
                        });
            }
        });
        context.start();

        s3Uploader.expectedMessageCount(2);
        s3Presigner.expectedMessageCount(2);
        s3Presigner.returnReplyBody(ExpressionBuilder.constantExpression("URL"));

        Exchange firstExchange = ExchangeBuilder.anExchange(context)
                .withHeader(AWS2S3Constants.KEY, "Key")
                .withHeader(AWS2S3Constants.DOWNLOAD_LINK_EXPIRATION_TIME, 300L)
                .withHeader("ResourceLinkDescription", "Compare Count Link")
                .withHeader("Upload", "mock:s3-uploader")
                .withHeader("Presign", "mock:s3-presigner")
                .withBody("CSV1")
                .build();

        Exchange secondExchange = ExchangeBuilder.anExchange(context)
                .withHeader(AWS2S3Constants.KEY, "Key")
                .withHeader(AWS2S3Constants.DOWNLOAD_LINK_EXPIRATION_TIME, 300L)
                .withHeader("ResourceLinkDescription", "Compare Collection Link")
                .withHeader("Upload", "mock:s3-uploader")
                .withHeader("Presign", "mock:s3-presigner")
                .withBody("CSV2")
                .build();


        kafkaEndpoint.expectedMessageCount(1);
        producerTemplate.send(firstExchange);
        producerTemplate.send(secondExchange);

        MockEndpoint.assertIsSatisfied(context);
    }
}
