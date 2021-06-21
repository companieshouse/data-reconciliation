package uk.gov.companieshouse.reconciliation.function.email.aggregator;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import uk.gov.companieshouse.reconciliation.config.AggregationHandler;
import uk.gov.companieshouse.reconciliation.config.ComparisonGroupModel;
import uk.gov.companieshouse.reconciliation.config.EmailLinkModel;
import uk.gov.companieshouse.reconciliation.model.ResourceLink;
import uk.gov.companieshouse.reconciliation.model.ResourceLinksWrapper;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmailAggregationStrategyTest {

    private EmailAggregationStrategy emailAggregationStrategy;
    private CamelContext context;

    @Mock
    private ComparisonGroupModel comparisonGroupModel;

    @Mock
    private AggregationHandler aggregationHandler;

    @Mock
    private Map<String, EmailLinkModel> emailLinkModelMap;

    @Mock
    private EmailLinkModel emailLinkModel;

    @BeforeEach
    void setUp() {
        emailAggregationStrategy = new EmailAggregationStrategy(aggregationHandler);
        context = new DefaultCamelContext();
    }

    @Test
    void testAddResourceLink() {
        //given
        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setHeader("LinkId", "LinkId");
        exchange.getIn().setHeader("ResourceLinkReference", "Link");
        exchange.getIn().setHeader("ResourceLinkDescription", "Description");
        exchange.getIn().setHeader("ComparisonGroup", "Company Profile");

        //when
        when(aggregationHandler.getAggregationConfiguration(anyString())).thenReturn(comparisonGroupModel);
        when(comparisonGroupModel.getEmailLinkModel()).thenReturn(emailLinkModelMap);
        when(emailLinkModelMap.get(anyString())).thenReturn(emailLinkModel);
        when(emailLinkModel.getRank()).thenReturn((short)10);

        Exchange result = emailAggregationStrategy.aggregate(null, exchange);
        ResourceLink wrapper = result.getIn().getHeader("ResourceLinks", ResourceLinksWrapper.class).getDownloadLinkList().get(0);

        //then
        assertEquals(exchange, result);
        assertEquals((short)10, wrapper.getRank());
        assertEquals("Link", wrapper.getDownloadLink());
        assertEquals("Description", wrapper.getDescription());
    }

    @Test
    void testCorrectlyAggregateMultipleResourceLinks() {
        //given
        ResourceLinksWrapper resourceLinksWrapper = new ResourceLinksWrapper(new TreeSet<>(Comparator.comparing(ResourceLink::getRank)));
        resourceLinksWrapper.addDownloadLink((short) 10, "Link1", "Description1");

        Exchange oldExchange = new DefaultExchange(context);
        oldExchange.getIn().setHeader("ResourceLinkReference", "Link1");
        oldExchange.getIn().setHeader("ResourceLinkDescription", "Description1");
        oldExchange.getIn().setHeader("ComparisonGroup", "Company Profile");
        oldExchange.getIn().setHeader("ResourceLinks", resourceLinksWrapper);
        oldExchange.getIn().setHeader("LinkId", "link-id-1");

        Exchange newExchange = new DefaultExchange(context);
        newExchange.getIn().setHeader("ResourceLinkReference", "Link2");
        newExchange.getIn().setHeader("ResourceLinkDescription", "Description2");
        newExchange.getIn().setHeader("ComparisonGroup", "Company Profile");
        newExchange.getIn().setHeader("LinkId", "link-id-2");

        //when
        when(aggregationHandler.getAggregationConfiguration(anyString())).thenReturn(comparisonGroupModel);
        when(comparisonGroupModel.getEmailLinkModel()).thenReturn(emailLinkModelMap);
        when(emailLinkModelMap.get("link-id-2")).thenReturn(emailLinkModel);
        when(emailLinkModel.getRank()).thenReturn((short)20);

        Exchange result = emailAggregationStrategy.aggregate(oldExchange, newExchange);
        ResourceLinksWrapper wrapper = result.getIn().getHeader("ResourceLinks", ResourceLinksWrapper.class);

        ResourceLink actual = wrapper.getDownloadLinkList().get(1);

        //then
        assertEquals(newExchange, result);
        assertEquals((short)20, actual.getRank());
        assertEquals("Link2", actual.getDownloadLink());
        assertEquals("Description2", actual.getDescription());
        assertEquals(2, wrapper.getDownloadLinkList().size());
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
