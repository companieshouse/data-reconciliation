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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class EmailAggregationStrategyTest {

    private EmailAggregationStrategy emailAggregationStrategy;
    private CamelContext context;

    @BeforeEach
    void setUp() {
        emailAggregationStrategy = new EmailAggregationStrategy();
        context = new DefaultCamelContext();
    }

    @Test
    void testAddResourceLinksContainingCompareCountLink() {
        //given
        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setHeader("CompareCountLink", "Link");
        exchange.getIn().setHeader("CompareCountDescription", "Description");

        //when
        Exchange result = emailAggregationStrategy.aggregate(null, exchange);
        ResourceLink wrapper = result.getIn().getHeader("ResourceLinks", ResourceLinksWrapper.class).getDownloadLinkList().get(0);

        //then
        assertEquals(exchange, result);
        assertEquals("Link", wrapper.getDownloadLink());
        assertEquals("Description", wrapper.getDescription());
    }

    @Test
    void testAddResourceLinksContainingCompareCollectionLink() {
        //given
        Exchange oldExchange = new DefaultExchange(context);
        oldExchange.getIn().setHeader("CompareCountLink", "CountLink");
        oldExchange.getIn().setHeader("CompareCountDescription", "CountDescription");
        List<ResourceLink> links = new ArrayList<>();
        links.add(new ResourceLink("Link", "Description"));
        oldExchange.getIn().setHeader("ResourceLinks", new ResourceLinksWrapper(links));
        Exchange newExchange = new DefaultExchange(context);
        newExchange.getIn().setHeader("CompareCollectionLink", "CollectionLink");
        newExchange.getIn().setHeader("CompareCollectionDescription", "CollectionDescription");

        //when
        Exchange result = emailAggregationStrategy.aggregate(oldExchange, newExchange);
        ResourceLinksWrapper wrapper = result.getIn().getHeader("ResourceLinks", ResourceLinksWrapper.class);
        ResourceLink compareCollectionLink = wrapper.getDownloadLinkList().get(1);

        //then
        assertEquals(oldExchange, result);
        assertEquals("CollectionLink", compareCollectionLink.getDownloadLink());
        assertEquals("CollectionDescription", compareCollectionLink.getDescription());
    }

    @Test
    void testThrowIllegalArgumentExceptionIfNeitherHeaderPresent() {
        //given
        Exchange exchange = new DefaultExchange(context);

        //when
        Executable actual = () -> emailAggregationStrategy.aggregate(null, exchange);

        //then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, actual);
        assertEquals("Expected links not present: CompareCountLink, CompareCollectionLink", exception.getMessage());
    }
}
