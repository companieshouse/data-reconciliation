//package uk.gov.companieshouse.reconciliation.email;
//
//import org.apache.camel.CamelContext;
//import org.apache.camel.Exchange;
//import org.apache.camel.impl.DefaultCamelContext;
//import org.apache.camel.support.DefaultExchange;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import uk.gov.companieshouse.reconciliation.function.email.EmailLinksSorter;
//import uk.gov.companieshouse.reconciliation.function.email.PublisherResourceRequest;
//import uk.gov.companieshouse.reconciliation.function.email.PublisherResourceRequestWrapper;
//import java.util.Arrays;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//public class EmailLinksSorterTest {
//
//    private EmailLinksSorter emailLinksSorter;
//
//    private CamelContext context;
//
//    @BeforeEach
//    void setUp() {
//        emailLinksSorter = new EmailLinksSorter();
//        context = new DefaultCamelContext();
//    }
//
//    @Test
//    void testPublisherResourceRequestListIsOrderedProperlyForElasticsearchGroup() {
//        // given
//        Exchange exchange = new DefaultExchange(context);
//        exchange.getIn().setHeader("ComparisonGroup", "Elasticsearch");
//        exchange.getIn().setHeader("PublisherResourceRequests",
//                new PublisherResourceRequestWrapper(Arrays.asList(
//                        new PublisherResourceRequest("key3", 300L, "uploaderEndpoint3", "presignerEndpoint3", "resourceLinkDescription3", "results3".getBytes(), "Elasticsearch", 3),
//                        new PublisherResourceRequest("key1", 300L, "uploaderEndpoint1", "presignerEndpoint1", "resourceLinkDescription1", "results1".getBytes(), "Elasticsearch", 1),
//                        new PublisherResourceRequest("key2", 300L, "uploaderEndpoint2", "presignerEndpoint2", "resourceLinkDescription2", "results2".getBytes(), "Elasticsearch", 2),
//                        new PublisherResourceRequest("key4", 300L, "uploaderEndpoint4", "presignerEndpoint4", "resourceLinkDescription4", "results4".getBytes(), "Elasticsearch", 4)
//                ))
//        );
//
//        // when
//        emailLinksSorter.map(exchange);
//
//        // then
//        assertEquals(new PublisherResourceRequestWrapper(Arrays.asList(
//                new PublisherResourceRequest("key1", 300L, "uploaderEndpoint1", "presignerEndpoint1", "resourceLinkDescription1", "results1".getBytes(), "Elasticsearch", 1),
//                new PublisherResourceRequest("key2", 300L, "uploaderEndpoint2", "presignerEndpoint2", "resourceLinkDescription2", "results2".getBytes(), "Elasticsearch", 2),
//                new PublisherResourceRequest("key3", 300L, "uploaderEndpoint3", "presignerEndpoint3", "resourceLinkDescription3", "results3".getBytes(), "Elasticsearch", 3),
//                new PublisherResourceRequest("key4", 300L, "uploaderEndpoint4", "presignerEndpoint4", "resourceLinkDescription4", "results4".getBytes(), "Elasticsearch", 4)
//        )), exchange.getIn().getHeader("PublisherResourceRequests"));
//    }
//
//    @Test
//    void testPublisherResourceRequestListIsOrderedProperlyForCompanyProfileGroup() {
//        // given
//        Exchange exchange = new DefaultExchange(context);
//        exchange.getIn().setHeader("ComparisonGroup", "Company profile");
//        exchange.getIn().setHeader("PublisherResourceRequests",
//                new PublisherResourceRequestWrapper(Arrays.asList(
//                        new PublisherResourceRequest("key3", 300L, "uploaderEndpoint3", "presignerEndpoint3", "resourceLinkDescription3", "results3".getBytes(), "Company profile", 3),
//                        new PublisherResourceRequest("key1", 300L, "uploaderEndpoint1", "presignerEndpoint1", "resourceLinkDescription1", "results1".getBytes(), "Company profile", 1),
//                        new PublisherResourceRequest("key2", 300L, "uploaderEndpoint2", "presignerEndpoint2", "resourceLinkDescription2", "results2".getBytes(), "Company profile", 2)
//                ))
//        );
//
//        // when
//        emailLinksSorter.map(exchange);
//
//        // then
//        assertEquals(new PublisherResourceRequestWrapper(Arrays.asList(
//                new PublisherResourceRequest("key1", 300L, "uploaderEndpoint1", "presignerEndpoint1", "resourceLinkDescription1", "results1".getBytes(), "Company profile", 1),
//                new PublisherResourceRequest("key2", 300L, "uploaderEndpoint2", "presignerEndpoint2", "resourceLinkDescription2", "results2".getBytes(), "Company profile", 2),
//                new PublisherResourceRequest("key3", 300L, "uploaderEndpoint3", "presignerEndpoint3", "resourceLinkDescription3", "results3".getBytes(), "Company profile", 3)
//        )), exchange.getIn().getHeader("PublisherResourceRequests"));
//    }
//}
