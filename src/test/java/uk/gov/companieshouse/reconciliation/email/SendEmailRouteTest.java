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
@TestPropertySource(locations = {"classpath:application-stubbed.properties", "classpath:comparison-groups.properties"})
@UseAdviceWith
public class SendEmailRouteTest {

    @Autowired
    private ModelCamelContext context;

    @EndpointInject("mock:kafka-endpoint")
    private MockEndpoint kafkaEndpoint;

    @EndpointInject("mock:s3-uploader")
    private MockEndpoint s3Uploader;

    @EndpointInject("mock:s3-presigner")
    private MockEndpoint s3Presigner;

    @Produce("direct:send-email")
    private ProducerTemplate producerTemplate;

    @AfterEach
    void after() {
        s3Uploader.reset();
        s3Presigner.reset();
        kafkaEndpoint.reset();
    }

    @Test
    void testSendEmailAggregatesMessageGroups() throws Exception {
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

        s3Uploader.expectedMessageCount(6);
        s3Presigner.expectedMessageCount(6);
        s3Presigner.returnReplyBody(ExpressionBuilder.constantExpression("URL"));

        Exchange firstCompanyExchange = buildExchange("Key", 300L, "Company profile", "Compare Count Link", "apple", 1);
        Exchange secondCompanyExchange = buildExchange("Key", 300L, "Company profile", "Compare Collection Link", "orange", 2);

        Exchange firstDsqExchange = buildExchange("Key", 300L, "Disqualified officer", "Disqualified Officer Link 1", "pear", 1);
        Exchange secondDsqExchange = buildExchange("Key", 300L, "Disqualified officer", "Disqualified Officer Link 2", "carrot", 2);

        Exchange firstElasticsearchExchange = buildExchange("Key", 300L, "Elasticsearch", "Elasticsearch link 1", "strawberry", 1);
        Exchange secondElasticsearchExchange = buildExchange("Key", 300L, "Elasticsearch", "Elasticsearch link 2", "raspberry", 2);

        kafkaEndpoint.expectedMessageCount(3);

        producerTemplate.send(firstCompanyExchange);
        producerTemplate.send(secondCompanyExchange);
        producerTemplate.send(firstDsqExchange);
        producerTemplate.send(secondDsqExchange);
        producerTemplate.send(firstElasticsearchExchange);
        producerTemplate.send(secondElasticsearchExchange);

        MockEndpoint.assertIsSatisfied(context);
    }

    private Exchange buildExchange(String key, long expirationTime, String group, String linkDescription, String body, Integer orderNumber) {
        return ExchangeBuilder.anExchange(context)
                .withHeader(AWS2S3Constants.KEY, key)
                .withHeader(AWS2S3Constants.DOWNLOAD_LINK_EXPIRATION_TIME, expirationTime)
                .withHeader("ComparisonGroup", group)
                .withHeader("OrderNumber", orderNumber)
                .withHeader("ResourceLinkDescription", linkDescription)
                .withHeader("Upload", "mock:s3-uploader")
                .withHeader("Presign", "mock:s3-presigner")
                .withBody(body)
                .build();
    }
}
