package uk.gov.companieshouse.reconciliation.function.email.aggregator;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.reconciliation.model.ResourceLink;
import uk.gov.companieshouse.reconciliation.model.ResourceLinksWrapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class CompanyEmailAggregationStrategyTest {

    private CompanyEmailAggregationStrategy emailAggregationStrategy;
    private CamelContext context;

    @BeforeEach
    void setUp() {
        emailAggregationStrategy = new CompanyEmailAggregationStrategy();
        context = new DefaultCamelContext();
    }

    @Test
    void testAddResourceLink() {
        //given
        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setHeader("ResourceLinkReference", "Link");
        exchange.getIn().setHeader("ResourceLinkDescription", "Description");

        //when
        Exchange result = emailAggregationStrategy.aggregate(null, exchange);
        ResourceLink wrapper = result.getIn().getHeader("ResourceLinks", ResourceLinksWrapper.class).getDownloadLinkList().get(0);

        //then
        assertEquals(exchange, result);
        assertEquals("Link", wrapper.getDownloadLink());
        assertEquals("Description", wrapper.getDescription());
    }

    @Test
    void testThrowIllegalArgumentExceptionIfResourceLinkAbsent() {
        //given
        Exchange exchange = new DefaultExchange(context);

        //when
        Executable actual = () -> emailAggregationStrategy.aggregate(null, exchange);

        //then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, actual);
        assertEquals("Mandatory header not present: ResourceLinkReference", exception.getMessage());
    }
}
