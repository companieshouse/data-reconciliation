package uk.gov.companieshouse.reconciliation.function.email.aggregator;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.reconciliation.config.AggregationHandler;
import uk.gov.companieshouse.reconciliation.function.email.PublisherResourceRequest;
import uk.gov.companieshouse.reconciliation.function.email.PublisherResourceRequestWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(MockitoExtension.class)
public class S3EmailPublisherAggregationStrategyTest {

    private S3EmailPublisherAggregationStrategy aggregationStrategy;

    private CamelContext context;

    @Mock
    private AggregationHandler aggregationHandler;

    @BeforeEach
    void setup() {
        context = new DefaultCamelContext();
        aggregationStrategy = new S3EmailPublisherAggregationStrategy(aggregationHandler);
    }

    @Test
    void testAggregateFirstMessage() {
        // given
        Exchange curr = new DefaultExchange(context);
        curr.getIn().setHeader(AWS2S3Constants.KEY, "key");
        curr.getIn().setHeader(AWS2S3Constants.DOWNLOAD_LINK_EXPIRATION_TIME, 300L);
        curr.getIn().setHeader("Upload", "uploaderEndpoint");
        curr.getIn().setHeader("Presign", "presignerEndpoint");
        curr.getIn().setHeader("ResourceLinkDescription", "resourceLinkDescription");
        curr.getIn().setBody("results".getBytes());

        // when
        Exchange actual = aggregationStrategy.aggregate(null, curr);

        // then
        assertEquals(new PublisherResourceRequestWrapper(Collections.singletonList(new PublisherResourceRequest("key", 300L, "uploaderEndpoint", "presignerEndpoint", "resourceLinkDescription", "results".getBytes()))), actual.getIn().getHeader("PublisherResourceRequests"));
    }

    @Test
    void testAggregateFurtherMessages() {
        // given
        Exchange prev = new DefaultExchange(context);
        prev.getIn().setHeader("PublisherResourceRequests", new PublisherResourceRequestWrapper(new ArrayList<>(Collections.singletonList(new PublisherResourceRequest("key", 300L, "uploaderEndpoint", "presignerEndpoint", "resourceLinkDescription", "results".getBytes())))));

        Exchange curr = new DefaultExchange(context);
        curr.getIn().setHeader(AWS2S3Constants.KEY, "key2");
        curr.getIn().setHeader(AWS2S3Constants.DOWNLOAD_LINK_EXPIRATION_TIME, 301L);
        curr.getIn().setHeader("Upload", "uploaderEndpoint2");
        curr.getIn().setHeader("Presign", "presignerEndpoint2");
        curr.getIn().setHeader("ResourceLinkDescription", "resourceLinkDescription2");
        curr.getIn().setBody("results2".getBytes());

        // when
        Exchange actual = aggregationStrategy.aggregate(prev, curr);

        // then
        assertEquals(new PublisherResourceRequestWrapper(Arrays.asList(new PublisherResourceRequest("key", 300L, "uploaderEndpoint", "presignerEndpoint", "resourceLinkDescription", "results".getBytes()), new PublisherResourceRequest("key2", 301L, "uploaderEndpoint2", "presignerEndpoint2", "resourceLinkDescription2", "results2".getBytes()))), actual.getIn().getHeader("PublisherResourceRequests"));
        assertSame(prev, actual);
    }



}
