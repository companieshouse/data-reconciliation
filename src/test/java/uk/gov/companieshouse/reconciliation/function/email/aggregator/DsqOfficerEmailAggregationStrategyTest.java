package uk.gov.companieshouse.reconciliation.function.email.aggregator;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import uk.gov.companieshouse.reconciliation.model.ResourceLinksWrapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DsqOfficerEmailAggregationStrategyTest {

    private DsqOfficerEmailAggregationStrategy aggregationStrategy;

    private CamelContext context;

    @BeforeEach
    void setUp() {
        aggregationStrategy = new DsqOfficerEmailAggregationStrategy();
        context = new DefaultCamelContext();
    }

    @Test
    void testConstructDownloadLinks() {
        //given
        Exchange next = new DefaultExchange(context);
        next.getIn().setHeader("ResourceLinkReference", "link");
        next.getIn().setHeader("ResourceLinkDescription", "description");

        //when
        Exchange result = aggregationStrategy.aggregate(null, next);
        ResourceLinksWrapper links = result.getIn().getHeader("ResourceLinks", ResourceLinksWrapper.class);

        //then
        assertEquals(1, links.getDownloadLinkList().size());
        assertEquals("link", links.getDownloadLinkList().get(0).getDownloadLink());
        assertEquals("description", links.getDownloadLinkList().get(0).getDescription());
    }

    @Test
    void throwIllegalArgumentExceptionIfLinkNotPresent() {
        //given
        Exchange next = new DefaultExchange(context);

        //when
        Executable actual = () -> aggregationStrategy.aggregate(null, next);

        //then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, actual);
        assertEquals("Expected link not present: ResourceLinkReference", exception.getMessage());
    }
}
