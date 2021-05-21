package uk.gov.companieshouse.reconciliation.service.mongo;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.support.builder.ExpressionBuilder;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;
import uk.gov.companieshouse.reconciliation.model.ResultModel;
import uk.gov.companieshouse.reconciliation.model.Results;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@CamelSpringBootTest
@SpringBootTest
@DirtiesContext
@TestPropertySource(locations = "classpath:application-stubbed.properties")
public class MongoCompanyNumberMapperTest {

    @Autowired
    private CamelContext context;

    @Produce("direct:mongo-company_number-mapper")
    private ProducerTemplate producerTemplate;

    @EndpointInject("mock:mongoCompanyProfileCollection")
    private MockEndpoint mongoEndpoint;

    @AfterEach
    void tearDown() {
        mongoEndpoint.reset();
    }

    @Test
    void testFetchResultsFromMongoAndTransformIntoResourceList() throws InterruptedException {
        Results expectedResults = new Results(Collections.singletonList(new ResultModel("12345678", "ACME LIMITED")));
        mongoEndpoint.returnReplyBody(ExpressionBuilder.constantExpression(expectedResults));
        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setHeader("MongoDescription", "description");
        exchange.getIn().setHeader("MongoTargetHeader", "target");
        Exchange result = producerTemplate.send(exchange);
        ResourceList actual = result.getIn().getHeader("target", ResourceList.class);
        assertEquals("description", actual.getResultDesc());
        assertTrue(actual.contains("12345678"));
        MockEndpoint.assertIsSatisfied(context);
    }
}