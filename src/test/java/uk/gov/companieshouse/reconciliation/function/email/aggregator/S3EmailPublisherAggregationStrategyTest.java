package uk.gov.companieshouse.reconciliation.function.email.aggregator;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.reconciliation.config.AggregationHandler;
import uk.gov.companieshouse.reconciliation.config.AggregationGroupModel;
import uk.gov.companieshouse.reconciliation.function.email.PublisherResourceRequest;
import uk.gov.companieshouse.reconciliation.function.email.PublisherResourceRequestWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class S3EmailPublisherAggregationStrategyTest {

    private S3EmailPublisherAggregationStrategy aggregationStrategy;

    private CamelContext context;

    @Mock
    private AggregationHandler aggregationHandler;

    @Mock
    private AggregationGroupModel aggregationGroupModel;

    @BeforeEach
    void setup() {
        context = new DefaultCamelContext();
        aggregationStrategy = new S3EmailPublisherAggregationStrategy(aggregationHandler);
    }

    @Test
    void testAggregateAndCompleteMessage() {
        // given
        Exchange curr = new DefaultExchange(context);
        curr.getIn().setHeader("ComparisonGroup", "group");
        curr.getIn().setHeader(AWS2S3Constants.KEY, "key");
        curr.getIn().setHeader(AWS2S3Constants.DOWNLOAD_LINK_EXPIRATION_TIME, 300L);
        curr.getIn().setHeader("Upload", "uploaderEndpoint");
        curr.getIn().setHeader("Presign", "presignerEndpoint");
        curr.getIn().setHeader("ResourceLinkDescription", "resourceLinkDescription");
        curr.getIn().setHeader("AggregationModelId", "AggregationModelId");
        curr.getIn().setBody("results".getBytes());

        when(aggregationHandler.getAggregationConfiguration(anyString())).thenReturn(aggregationGroupModel);
        when(aggregationGroupModel.getEnabledAggregationModelsSize()).thenReturn(1);

        // when
        Exchange actual = aggregationStrategy.aggregate(null, curr);

        // then
        assertEquals(new PublisherResourceRequestWrapper(Collections.singletonList(new PublisherResourceRequest("key", 300L, "uploaderEndpoint", "presignerEndpoint", "resourceLinkDescription", "results".getBytes(), "group", "AggregationModelId", false))), actual.getIn().getHeader("PublisherResourceRequests"));
        assertTrue(actual.getIn().getHeader("Completed", Boolean.class));
        verify(aggregationHandler).getAggregationConfiguration("group");
    }

    @Test
    void testAggregateFurtherMessages() {
        // given
        Exchange prev = new DefaultExchange(context);
        prev.getIn().setHeader("PublisherResourceRequests", new PublisherResourceRequestWrapper(new ArrayList<>(Collections.singletonList(new PublisherResourceRequest("key", 300L, "uploaderEndpoint", "presignerEndpoint", "resourceLinkDescription", "results".getBytes(), "group", "aggregationModelId", false)))));

        Exchange curr = new DefaultExchange(context);
        curr.getIn().setHeader("ComparisonGroup", "group");
        curr.getIn().setHeader(AWS2S3Constants.KEY, "key2");
        curr.getIn().setHeader(AWS2S3Constants.DOWNLOAD_LINK_EXPIRATION_TIME, 301L);
        curr.getIn().setHeader("Upload", "uploaderEndpoint2");
        curr.getIn().setHeader("Presign", "presignerEndpoint2");
        curr.getIn().setHeader("ResourceLinkDescription", "resourceLinkDescription2");
        curr.getIn().setHeader("AggregationModelId", "aggregationModelId2");
        curr.getIn().setHeader("Failed", true);
        curr.getIn().setBody("results2".getBytes());

        when(aggregationHandler.getAggregationConfiguration(anyString())).thenReturn(aggregationGroupModel);
        when(aggregationGroupModel.getEnabledAggregationModelsSize()).thenReturn(2);

        // when
        Exchange actual = aggregationStrategy.aggregate(prev, curr);

        // then
        assertEquals(new PublisherResourceRequestWrapper(Arrays.asList(new PublisherResourceRequest("key", 300L, "uploaderEndpoint", "presignerEndpoint", "resourceLinkDescription", "results".getBytes(), "group", "aggregationModelId", false), new PublisherResourceRequest("key2", 301L, "uploaderEndpoint2", "presignerEndpoint2", "resourceLinkDescription2", "results2".getBytes(), "group", "aggregationModelId2", true))), actual.getIn().getHeader("PublisherResourceRequests"));
        assertSame(prev, actual);
        assertNull(actual.getIn().getHeader("Completed"));
        verify(aggregationHandler).getAggregationConfiguration("group");
    }

    @Test
    void testIllegalArgumentExceptionIsThrowIfAggregationGroupModelAbsent() {
        // given
        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setHeader("ComparisonGroup", "group");

        // when
        Executable actual = () -> aggregationStrategy.aggregate(null, exchange);

        // then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, actual);
        assertEquals("Mandatory configuration not present AggregationGroupModel: group", exception.getMessage());
    }

    @Test
    void testIllegalArgumentExceptionIsThrownIfComparisonGroupUnhandled() {
        // given
        Exchange prev = new DefaultExchange(context);
        prev.getIn().setHeader("PublisherResourceRequests", new PublisherResourceRequestWrapper(new ArrayList<>(Collections.singletonList(new PublisherResourceRequest("key", 300L, "uploaderEndpoint", "presignerEndpoint", "resourceLinkDescription", "results".getBytes(), "group", "AggregationModelId", false)))));

        Exchange curr = new DefaultExchange(context);
        curr.getIn().setHeader("ComparisonGroup", "group");

        // when
        Executable actual = () -> aggregationStrategy.aggregate(prev, curr);

        // then
        assertThrows(IllegalArgumentException.class, actual);
        verify(aggregationHandler).getAggregationConfiguration("group");
    }
}
