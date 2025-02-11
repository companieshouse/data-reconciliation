package uk.gov.companieshouse.reconciliation.service.elasticsearch;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExpressionBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import uk.gov.companieshouse.reconciliation.config.aws.S3ClientConfig;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;
import uk.gov.companieshouse.reconciliation.model.ResultModel;
import uk.gov.companieshouse.reconciliation.model.Results;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@CamelSpringBootTest
@SpringBootTest
@DirtiesContext
@TestPropertySource(locations = "classpath:application-stubbed.properties")
@Import(S3ClientConfig.class)
public class ElasticsearchCompanyNumberMapperTest {

    @Autowired
    private CamelContext context;

    @EndpointInject("mock:elasticsearch-collection")
    private MockEndpoint elasticsearchServiceWrapper;

    @Produce("direct:elasticsearch-company_number-mapper")
    private ProducerTemplate producerTemplate;

    @AfterEach
    void tearDown() {
        elasticsearchServiceWrapper.reset();
    }

    @Test
    void testFetchSearchIndicesAndTransformIntoCompanyNumbers() throws InterruptedException {
        ResultModel expected = new ResultModel("12345678", "ACME LIMITED");
        Exchange request = new DefaultExchange(context);
        request.getIn().setHeader("ElasticsearchEndpoint", "mock:elasticsearch-wrapper");
        request.getIn().setHeader("Description", "Description");
        elasticsearchServiceWrapper.returnReplyBody(ExpressionBuilder.constantExpression(new Results(Collections.singletonList(expected))));
        elasticsearchServiceWrapper.expectedMessageCount(1);
        Exchange exchange = producerTemplate.send(request);
        ResourceList actual = exchange.getIn().getBody(ResourceList.class);
        assertTrue(actual.getResultList().contains("12345678"));
        assertEquals("Description", actual.getResultDesc());
        MockEndpoint.assertIsSatisfied(context);
    }

    @Test
    void testSkipTransformationIfFailureHeaderSet() {
        Exchange request = new DefaultExchange(context);
        request.getIn().setHeader("ElasticsearchEndpoint", "mock:elasticsearch-wrapper");
        request.getIn().setHeader("Description", "Description");
        elasticsearchServiceWrapper.returnReplyHeader("Failed", ExpressionBuilder.constantExpression(true));
        elasticsearchServiceWrapper.expectedMessageCount(1);
        Exchange exchange = producerTemplate.send(request);
        assertNull(exchange.getIn().getBody());
        assertNull(exchange.getProperty("CamelExceptionCaught"));
    }
}
