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
import uk.gov.companieshouse.reconciliation.config.AggregationHandler;
import uk.gov.companieshouse.reconciliation.config.AggregationGroupModel;
import uk.gov.companieshouse.reconciliation.config.AggregationModel;
import uk.gov.companieshouse.reconciliation.model.ResourceLink;
import uk.gov.companieshouse.reconciliation.model.ResourceLinksWrapper;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmailAggregationStrategyTest {

    private EmailAggregationStrategy emailAggregationStrategy;
    private CamelContext context;

    @Mock
    private AggregationGroupModel aggregationGroupModel;

    @Mock
    private AggregationHandler aggregationHandler;

    @Mock
    private Map<String, AggregationModel> aggregationModels;

    @Mock
    private AggregationModel aggregationModel;

    @BeforeEach
    void setUp() {
        emailAggregationStrategy = new EmailAggregationStrategy(aggregationHandler);
        context = new DefaultCamelContext();
    }

    @Test
    void testAddResourceLink() {
        //given
        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setHeader("AggregationModelId", "aggregationModelId");
        exchange.getIn().setHeader("ResourceLinkReference", "Link");
        exchange.getIn().setHeader("ResourceLinkDescription", "Description");
        exchange.getIn().setHeader("ComparisonGroup", "Company Profile");

        //when
        when(aggregationHandler.getAggregationConfiguration(anyString())).thenReturn(aggregationGroupModel);
        when(aggregationGroupModel.getAggregationModels()).thenReturn(aggregationModels);
        when(aggregationModels.get(anyString())).thenReturn(aggregationModel);
        when(aggregationModel.getLinkRank()).thenReturn((short)10);


        Exchange result = emailAggregationStrategy.aggregate(null, exchange);
        ResourceLinksWrapper wrapper = result.getIn().getHeader("ResourceLinks", ResourceLinksWrapper.class);

        ResourceLink actual = wrapper.getDownloadLinkSet().stream()
                .filter(resourceLink -> resourceLink.getRank() == 10)
                .findFirst()
                .get();

        //then
        assertEquals(exchange, result);
        assertEquals((short)10, actual.getRank());
        assertEquals("Link", actual.getDownloadLink());
        assertEquals("Description", actual.getDescription());
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
        oldExchange.getIn().setHeader("AggregationModelId", "link-id-1");

        Exchange newExchange = new DefaultExchange(context);
        newExchange.getIn().setHeader("ResourceLinkReference", "Link2");
        newExchange.getIn().setHeader("ResourceLinkDescription", "Description2");
        newExchange.getIn().setHeader("ComparisonGroup", "Company Profile");
        newExchange.getIn().setHeader("AggregationModelId", "link-id-2");

        when(aggregationHandler.getAggregationConfiguration(anyString())).thenReturn(aggregationGroupModel);
        when(aggregationGroupModel.getAggregationModels()).thenReturn(aggregationModels);
        when(aggregationModels.get("link-id-2")).thenReturn(aggregationModel);
        when(aggregationModel.getLinkRank()).thenReturn((short)20);

        //when
        Exchange result = emailAggregationStrategy.aggregate(oldExchange, newExchange);
        ResourceLinksWrapper wrapper = result.getIn().getHeader("ResourceLinks", ResourceLinksWrapper.class);

        ResourceLink actual = wrapper.getDownloadLinkSet().stream()
                .filter(resourceLink -> resourceLink.getRank() == 20)
                .findFirst()
                .get();

        //then
        assertEquals(newExchange, result);
        assertEquals((short)20, actual.getRank());
        assertEquals("Link2", actual.getDownloadLink());
        assertEquals("Description2", actual.getDescription());
        assertEquals(2, wrapper.getDownloadLinkSet().size());
    }

    @Test
    void testCorrectlyUseANullLinkWhenResourceLinkReferenceAbsentButResourceLinkDescriptionIsPresent() {
        //given
        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setHeader("ResourceLinkDescription", "Description");
        exchange.getIn().setHeader("ComparisonGroup", "any");
        exchange.getIn().setHeader("AggregationModelId", "any");

        when(aggregationHandler.getAggregationConfiguration(anyString())).thenReturn(aggregationGroupModel);
        when(aggregationGroupModel.getAggregationModels()).thenReturn(aggregationModels);
        when(aggregationModels.get(anyString())).thenReturn(aggregationModel);
        when(aggregationModel.getLinkRank()).thenReturn((short)10);

        //when
        Exchange result = emailAggregationStrategy.aggregate(null, exchange);
        ResourceLinksWrapper wrapper = result.getIn().getHeader("ResourceLinks", ResourceLinksWrapper.class);

        ResourceLink actual = wrapper.getDownloadLinkSet().stream()
                .filter(resourceLink -> resourceLink.getRank() == 10)
                .findFirst()
                .get();

        //then
        assertEquals(exchange, result);
        assertNull(actual.getDownloadLink());
        assertEquals("Description", actual.getDescription());
    }

    @Test
    void testThrowIllegalStateExceptionIfLinkReferenceAndDescriptionAbsent() {
        //given
        Exchange exchange = new DefaultExchange(context);

        //when
        Executable actual = () -> emailAggregationStrategy.aggregate(null, exchange);

        //then
        assertThrows(IllegalStateException.class, actual);
    }

    @Test
    void testThrowIllegalArgumentExceptionIfAggregationModelIdAbsent() {
        //given
        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setHeader("ResourceLinkReference", "Link");

        //when
        Executable actual = () -> emailAggregationStrategy.aggregate(null, exchange);

        //then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, actual);
        assertEquals("Mandatory header not present: AggregationModelId", exception.getMessage());
    }

    @Test
    void testThrowIllegalArgumentExceptionIfComparisonGroupAbsent() {
        //given
        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setHeader("ResourceLinkReference", "Link");
        exchange.getIn().setHeader("AggregationModelId", "aggregationModelId");

        //when
        Executable actual = () -> emailAggregationStrategy.aggregate(null, exchange);

        //then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, actual);
        assertEquals("Mandatory header not present: ComparisonGroup", exception.getMessage());
    }

    @Test
    void testThrowIllegalArgumentExceptionIfAggregationGroupModelAbsent() {
        //given
        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setHeader("ResourceLinkReference", "Link");
        exchange.getIn().setHeader("AggregationModelId", "aggregationModelId");
        exchange.getIn().setHeader("ComparisonGroup", "Comparison group");

        //when
        Executable actual = () -> emailAggregationStrategy.aggregate(null, exchange);

        //then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, actual);
        assertEquals("Mandatory AggregationGroupModel configuration not present: Comparison group", exception.getMessage());
    }

    @Test
    void testThrowIllegalArgumentExceptionIfAggregationModelAbsent() {
        //given
        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setHeader("ResourceLinkReference", "Link");
        exchange.getIn().setHeader("AggregationModelId", "aggregationModelId");
        exchange.getIn().setHeader("ComparisonGroup", "Comparison group");

        when(aggregationHandler.getAggregationConfiguration(anyString())).thenReturn(aggregationGroupModel);

        //when
        Executable actual = () -> emailAggregationStrategy.aggregate(null, exchange);

        //then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, actual);
        assertEquals("Mandatory AggregationModel configuration not present: aggregationModelId", exception.getMessage());
    }
}
