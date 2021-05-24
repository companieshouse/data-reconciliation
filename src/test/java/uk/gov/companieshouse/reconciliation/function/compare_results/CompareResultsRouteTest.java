package uk.gov.companieshouse.reconciliation.function.compare_results;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExpressionBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import uk.gov.companieshouse.reconciliation.model.ResultModel;
import uk.gov.companieshouse.reconciliation.model.Results;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@CamelSpringBootTest
@SpringBootTest
@DirtiesContext
@TestPropertySource(locations = "classpath:application-stubbed.properties")
public class CompareResultsRouteTest {

    @Autowired
    private CamelContext context;

    @Produce("direct:compare_results")
    private ProducerTemplate producerTemplate;

    @EndpointInject("mock:src-endpoint")
    private MockEndpoint srcEndpoint;

    @EndpointInject("mock:target-endpoint")
    private MockEndpoint targetEndpoint;

    @EndpointInject("mock:log-result")
    private MockEndpoint output;

    @AfterEach
    void after() {
        srcEndpoint.reset();
        targetEndpoint.reset();
        output.reset();
    }

    @Test
    void testIdentifyDifferentCompanyNames() throws InterruptedException {
        // given
        Results srcResults = new Results(Arrays.asList(new ResultModel("12345678", "ACME LTD"), new ResultModel("23456789", "KICK CIC"), new ResultModel("ABCD1234", "UNLIMITED LTD")));
        Results targetResults = new Results(Arrays.asList(new ResultModel("12345678", "ACME LIMITED"), new ResultModel("23456780", "PRIVATE PLC"), new ResultModel("ABCD1234", "UNLIMITED LTD")));
        srcEndpoint.returnReplyBody(ExpressionBuilder.constantExpression(srcResults));
        targetEndpoint.returnReplyBody(ExpressionBuilder.constantExpression(targetResults));
        output.allMessages().body().isEqualTo("Company Number,MongoDB - Company Profile,Primary Search Index\r\n12345678,ACME LTD,ACME LIMITED\r\n");

        // when
        producerTemplate.sendBodyAndHeaders(0, createHeaders());
        MockEndpoint.assertIsSatisfied(context);
    }

    private Map<String, Object> createHeaders() {
        Map<String, Object> headers = new HashMap<>();
        headers.put("Src", "mock:src-endpoint");
        headers.put("Target", "mock:target-endpoint");
        headers.put("ResourceLinkDescription", "Description");
        headers.put("SrcDescription", "MongoDB - Company Profile");
        headers.put("TargetDescription", "Primary Search Index");
        headers.put("RecordType", "Company Number");
        headers.put("Destination", "mock:log-result");
        return headers;
    }

}
