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

    @EndpointInject("mock:transformer")
    private MockEndpoint transformer;

    @AfterEach
    void after() {
        srcEndpoint.reset();
        targetEndpoint.reset();
        output.reset();
        transformer.reset();
    }

    @Test
    void testIdentifyDifferentCompanyNames() throws InterruptedException {
        // given
        Results srcResults = new Results(Arrays.asList(new ResultModel("12345678", "ACME LTD"),
                new ResultModel("23456789", "KICK CIC"),
                new ResultModel("ABCD1234", "UNLIMITED LTD")));
        Results targetResults = new Results(
                Arrays.asList(new ResultModel("12345678", "ACME LIMITED"),
                        new ResultModel("23456780", "PRIVATE PLC"),
                        new ResultModel("ABCD1234", "UNLIMITED LTD")));
        srcEndpoint.returnReplyBody(ExpressionBuilder.constantExpression(srcResults));
        targetEndpoint.returnReplyBody(ExpressionBuilder.constantExpression(targetResults));
        output.allMessages().body().isEqualTo(
                "Company Number,MongoDB - Company Profile,Primary Search Index\r\n12345678,ACME LTD,ACME LIMITED\r\n");
        output.expectedHeaderReceived("ResourceLinkDescription",
                "Comparisons completed for companies in MongoDB - Company Profile and Primary Search Index.");

        transformer.expectedHeaderReceived("SrcList", srcResults);
        transformer.expectedHeaderReceived("SrcDescription", "MongoDB - Company Profile");
        transformer.expectedHeaderReceived("TargetList", targetResults);
        transformer.expectedHeaderReceived("TargetDescription", "Primary Search Index");
        transformer.expectedHeaderReceived("RecordKey", "Company Number");
        transformer.returnReplyBody(
                ExpressionBuilder.constantExpression(Arrays.asList(
                        new HashMap<String, Object>() {{
                            put("Company Number", "Company Number");
                            put("MongoDB - Company Profile", "MongoDB - Company Profile");
                            put("Primary Search Index", "Primary Search Index");
                        }},
                        new HashMap<String, Object>() {{
                            put("Company Number", "12345678");
                            put("MongoDB - Company Profile", "ACME LTD");
                            put("Primary Search Index", "ACME LIMITED");
                        }}
                )));

        // when
        producerTemplate.sendBodyAndHeaders(0, createHeaders());
        MockEndpoint.assertIsSatisfied(context);
    }

    @Test
    void testSetLinkDescriptionToFailureMessageIfComparisonSourceFails() throws InterruptedException {
        //given
        srcEndpoint.returnReplyHeader("Failed", ExpressionBuilder.constantExpression(true));
        output.expectedHeaderReceived("ResourceLinkDescription",
                "Failed to compare companies in MongoDB - Company Profile and Primary Search Index.");

        //when
        producerTemplate.sendBodyAndHeaders(0, createHeaders());

        //then
        MockEndpoint.assertIsSatisfied(context);
    }

    @Test
    void testSetLinkDescriptionToFailureMessageIfComparisonTargetFails() throws InterruptedException {
        // given
        Results srcResults = new Results(Arrays.asList(new ResultModel("12345678", "ACME LTD"),
                new ResultModel("23456789", "KICK CIC"),
                new ResultModel("ABCD1234", "UNLIMITED LTD")));
        srcEndpoint.returnReplyBody(ExpressionBuilder.constantExpression(srcResults));
        targetEndpoint.returnReplyHeader("Failed", ExpressionBuilder.constantExpression(true));
        output.expectedHeaderReceived("ResourceLinkDescription",
                "Failed to compare companies in MongoDB - Company Profile and Primary Search Index.");

        //when
        producerTemplate.sendBodyAndHeaders(0, createHeaders());

        //then
        MockEndpoint.assertIsSatisfied(context);
    }

    private Map<String, Object> createHeaders() {
        Map<String, Object> headers = new HashMap<>();
        headers.put("Src", "mock:src-endpoint");
        headers.put("Target", "mock:target-endpoint");
        headers.put("SrcDescription", "MongoDB - Company Profile");
        headers.put("TargetDescription", "Primary Search Index");
        headers.put("RecordKey", "Company Number");
        headers.put("Comparison", "companies");
        headers.put("Destination", "mock:log-result");
        headers.put("ResultsTransformer", "mock:transformer");
        return headers;
    }

}
