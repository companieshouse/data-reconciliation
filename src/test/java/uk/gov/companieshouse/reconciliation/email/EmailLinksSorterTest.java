package uk.gov.companieshouse.reconciliation.email;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.reconciliation.config.EmailLinkModel;
import uk.gov.companieshouse.reconciliation.function.email.EmailLinksSorter;
import uk.gov.companieshouse.reconciliation.model.ResourceLink;
import uk.gov.companieshouse.reconciliation.model.ResourceLinksWrapper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class EmailLinksSorterTest {

    private CamelContext context;
    private String emailId;
    private Map<String, Map<String, EmailLinkModel>> emailLinkModelMap;

    @BeforeEach
    void setUp() {
        context = new DefaultCamelContext();
        emailId = "email-id";
        emailLinkModelMap = Collections.unmodifiableMap(new HashMap<String, Map<String, EmailLinkModel>>() {{
            put(emailId, new HashMap<String, EmailLinkModel>(){{
                put("link-id-1", create((short) 10));
                put("link-id-2", create((short) 20));
                put("link-id-3", create((short) 30));
                put("link-id-4", create((short) 40));
                put("link-id-5", create((short) 50));
                put("link-id-6", create((short) 60));
                put("link-id-7", create((short) 70));
                put("link-id-8", create((short) 80));
                put("link-id-9", create((short) 90));
                put("link-id-10", create((short) 100));
            }});
        }});
    }
    public static EmailLinkModel create(short rank) {
        EmailLinkModel model = new EmailLinkModel();
        model.setRank(rank);
        return model;
    }
    @Test
    void testCorrectlyOrdersLinksWhenSortingIsNotNeeded() {
        // given
        EmailLinksSorter emailLinksSorter = new EmailLinksSorter(emailLinkModelMap);

        Exchange source = new DefaultExchange(context);
        source.getIn().setHeader(EmailLinksSorter.RESOURCE_LINKS_HEADER, new ResourceLinksWrapper(emailId,  Arrays.asList(
                new ResourceLink("link-id-1", "downloadlink-1", "description-1"),
                new ResourceLink("link-id-2", "downloadlink-2", "description-2"),
                new ResourceLink("link-id-3", "downloadlink-3", "description-3"),
                new ResourceLink("link-id-4", "downloadlink-4", "description-4"),
                new ResourceLink("link-id-5", "downloadlink-5", "description-5"),
                new ResourceLink("link-id-6", "downloadlink-6", "description-6"),
                new ResourceLink("link-id-7", "downloadlink-7", "description-7"),
                new ResourceLink("link-id-8", "downloadlink-8", "description-8"),
                new ResourceLink("link-id-9", "downloadlink-9", "description-9"),
                new ResourceLink("link-id-10", "downloadlink-10", "description-10")
        )));

        // when
        emailLinksSorter.map(source);

        // then
        ResourceLinksWrapper expected = new ResourceLinksWrapper(emailId,  Arrays.asList(
                new ResourceLink("link-id-1", "downloadlink-1", "description-1"),
                new ResourceLink("link-id-2", "downloadlink-2", "description-2"),
                new ResourceLink("link-id-3", "downloadlink-3", "description-3"),
                new ResourceLink("link-id-4", "downloadlink-4", "description-4"),
                new ResourceLink("link-id-5", "downloadlink-5", "description-5"),
                new ResourceLink("link-id-6", "downloadlink-6", "description-6"),
                new ResourceLink("link-id-7", "downloadlink-7", "description-7"),
                new ResourceLink("link-id-8", "downloadlink-8", "description-8"),
                new ResourceLink("link-id-9", "downloadlink-9", "description-9"),
                new ResourceLink("link-id-10", "downloadlink-10", "description-10")
        ));

        ResourceLinksWrapper actual = (ResourceLinksWrapper) source.getIn().getHeader(EmailLinksSorter.RESOURCE_LINKS_HEADER);

        assertEquals(expected, actual);
    }

    @Test
    void testCorrectlyOrdersLinksWhenSortingIsNeeded() {
        // given
        EmailLinksSorter emailLinksSorter = new EmailLinksSorter(emailLinkModelMap);

        Exchange source = new DefaultExchange(context);
        source.getIn().setHeader(EmailLinksSorter.RESOURCE_LINKS_HEADER, new ResourceLinksWrapper(emailId,  Arrays.asList(
                new ResourceLink("link-id-2", "downloadlink-2", "description-2"),
                new ResourceLink("link-id-10", "downloadlink-10", "description-10"),
                new ResourceLink("link-id-8", "downloadlink-8", "description-8"),
                new ResourceLink("link-id-3", "downloadlink-3", "description-3"),
                new ResourceLink("link-id-4", "downloadlink-4", "description-4"),
                new ResourceLink("link-id-1", "downloadlink-1", "description-1"),
                new ResourceLink("link-id-7", "downloadlink-7", "description-7"),
                new ResourceLink("link-id-5", "downloadlink-5", "description-5"),
                new ResourceLink("link-id-6", "downloadlink-6", "description-6"),
                new ResourceLink("link-id-9", "downloadlink-9", "description-9")
        )));

        // when
        emailLinksSorter.map(source);

        // then
        ResourceLinksWrapper expected = new ResourceLinksWrapper(emailId,  Arrays.asList(
                new ResourceLink("link-id-1", "downloadlink-1", "description-1"),
                new ResourceLink("link-id-2", "downloadlink-2", "description-2"),
                new ResourceLink("link-id-3", "downloadlink-3", "description-3"),
                new ResourceLink("link-id-4", "downloadlink-4", "description-4"),
                new ResourceLink("link-id-5", "downloadlink-5", "description-5"),
                new ResourceLink("link-id-6", "downloadlink-6", "description-6"),
                new ResourceLink("link-id-7", "downloadlink-7", "description-7"),
                new ResourceLink("link-id-8", "downloadlink-8", "description-8"),
                new ResourceLink("link-id-9", "downloadlink-9", "description-9"),
                new ResourceLink("link-id-10", "downloadlink-10", "description-10")
        ));

        ResourceLinksWrapper actual = (ResourceLinksWrapper) source.getIn().getHeader(EmailLinksSorter.RESOURCE_LINKS_HEADER);

        assertEquals(expected, actual);
    }
}
