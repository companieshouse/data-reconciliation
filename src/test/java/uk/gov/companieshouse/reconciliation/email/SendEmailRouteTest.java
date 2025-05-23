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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import uk.gov.companieshouse.reconciliation.config.aws.S3ClientConfig;
import uk.gov.companieshouse.reconciliation.model.ResourceLinksWrapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

@CamelSpringBootTest
@SpringBootTest
@DirtiesContext
@TestPropertySource(locations = {"classpath:application-stubbed.properties", "classpath:aggregation-groups.properties"})
@UseAdviceWith
@Import(S3ClientConfig.class)
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

    @BeforeEach
    void before() {
    }

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
                            assertEquals(2, downloadsList.getDownloadLinkSet().size());
                        });
            }
        });
        context.start();
        s3Uploader.expectedMessageCount(7);
        s3Uploader.whenExchangeReceived(2, exchange -> {
            throw AwsServiceException.builder().build();
        });
        s3Presigner.expectedMessageCount(6);
        s3Presigner.returnReplyBody(ExpressionBuilder.constantExpression("URL"));

        Exchange firstCompanyExchange = buildExchange("Key", 300L, "Company profile", "Compare Count Link", "apple", "company-count-mongo-oracle", true, "1.1");
        Exchange secondCompanyExchange = buildExchange("Key", 300L, "Company profile", "Compare Collection Link", "orange", "company-number-mongo-oracle", false, "1.2");

        Exchange firstDsqExchange = buildExchange("Key", 300L, "Disqualified officer", "Disqualified Officer Link 1", "pear", "dsq-officer-id-mongo-oracle", false, "2.1");
        Exchange secondDsqExchange = buildExchange("Key", 300L, "Disqualified officer", "Disqualified Officer Link 2", "carrot", "dsq-officer-id-mongo-oracle2", false, "2.2");

        Exchange firstElasticsearchExchange = buildExchange("Key", 300L, "Elasticsearch", "Elasticsearch link 1", "strawberry", "company-name-mongo-alpha", false, "3.1");
        Exchange secondElasticsearchExchange = buildExchange("Key", 300L, "Elasticsearch", "Elasticsearch link 2", "raspberry", "company-name-mongo-primary", false, "3.2");

        Exchange firstInsolvencyExchange = buildExchange("Key", 300L, "Company insolvency", "Insolvency link 1", "avocado", "insolvency-company-number-mongo-oracle", false, "4.1");
        Exchange secondInsolvencyExchange = buildExchange("Key", 300L, "Company insolvency", "Insolvency link 2", "kale", "insolvency-link2", false, "4.2");

        kafkaEndpoint.expectedMessageCount(4);

        producerTemplate.send(firstCompanyExchange);
        producerTemplate.send(secondCompanyExchange);
        producerTemplate.send(firstDsqExchange);
        producerTemplate.send(secondDsqExchange);
        producerTemplate.send(firstElasticsearchExchange);
        producerTemplate.send(secondElasticsearchExchange);
        producerTemplate.send(firstInsolvencyExchange);
        producerTemplate.send(secondInsolvencyExchange);

        MockEndpoint.assertIsSatisfied(context);
    }

    private Exchange buildExchange(String key, long expirationTime, String group, String linkDescription, String body, String aggregationModelId, boolean failed, String comparisonDescription) {

        return ExchangeBuilder.anExchange(context)
                .withHeader(AWS2S3Constants.KEY, key)
                .withHeader(AWS2S3Constants.DOWNLOAD_LINK_EXPIRATION_TIME, expirationTime)
                .withHeader("ComparisonGroup", group)
                .withHeader("ResourceLinkDescription", linkDescription)
                .withHeader("Upload", "mock:s3-uploader")
                .withHeader("Presign", "mock:s3-presigner")
                .withHeader("AggregationModelId", aggregationModelId)
                .withHeader("Failed", failed)
                .withHeader("ComparisonDescription", comparisonDescription)
                .withBody(body)
                .build();
    }
}
