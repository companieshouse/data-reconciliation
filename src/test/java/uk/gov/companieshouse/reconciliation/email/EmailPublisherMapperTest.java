package uk.gov.companieshouse.reconciliation.email;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.reconciliation.function.email.EmailPublisherMapper;
import uk.gov.companieshouse.reconciliation.function.email.PublisherResourceRequest;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmailPublisherMapperTest {

    private EmailPublisherMapper emailPublisherMapper;

    private CamelContext context;

    @BeforeEach
    void setUp() {
        emailPublisherMapper = new EmailPublisherMapper();
        context = new DefaultCamelContext();
    }

    @Test
    void testMapResourceRequestToExchange() {
        //given
        Exchange targetExchange = new DefaultExchange(context);
        PublisherResourceRequest request = new PublisherResourceRequest("key", 300, "uploader", "presigner", "description", "BODY".getBytes(), "group", "linkId");

        //when
        emailPublisherMapper.map(request, targetExchange);

        //then
        assertEquals("key", targetExchange.getIn().getHeader(AWS2S3Constants.KEY));
        assertEquals(300L, targetExchange.getIn().getHeader(AWS2S3Constants.DOWNLOAD_LINK_EXPIRATION_TIME));
        assertEquals("uploader", targetExchange.getIn().getHeader("Upload"));
        assertEquals("presigner", targetExchange.getIn().getHeader("Presign"));
        assertEquals("description", targetExchange.getIn().getHeader("ResourceLinkDescription"));
        assertEquals("group", targetExchange.getIn().getHeader("ComparisonGroup"));
        assertEquals("linkId", targetExchange.getIn().getHeader("LinkId"));
        assertArrayEquals("BODY".getBytes(), targetExchange.getIn().getBody(byte[].class));
    }
}
