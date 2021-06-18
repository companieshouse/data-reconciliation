package uk.gov.companieshouse.reconciliation.email;

import org.apache.camel.CamelContext;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.reconciliation.function.email.EmailPublisherMapper;
import uk.gov.companieshouse.reconciliation.function.email.PublisherResourceRequest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        Map<String, Object> headers = new HashMap<>();
        PublisherResourceRequest request = new PublisherResourceRequest("key", 300, "uploader", "presigner", "description", "BODY".getBytes(), "group", true);

        //when
        Object actual = emailPublisherMapper.map(request, headers);

        //then
        assertEquals("key", headers.get(AWS2S3Constants.KEY));
        assertEquals(300L, headers.get(AWS2S3Constants.DOWNLOAD_LINK_EXPIRATION_TIME));
        assertEquals("uploader", headers.get("Upload"));
        assertEquals("presigner", headers.get("Presign"));
        assertEquals("description", headers.get("ResourceLinkDescription"));
        assertArrayEquals("BODY".getBytes(), (byte[])actual);
        assertTrue((boolean)headers.get("Failed"));
    }

    @Test
    void testMapResourceRequestToExchangeSkipFailedIfFalse() {
        //given
        Map<String, Object> headers = new HashMap<>();
        PublisherResourceRequest request = new PublisherResourceRequest("key", 300, "uploader", "presigner", "description", "BODY".getBytes(), "group", false);

        //when
        Object actual = emailPublisherMapper.map(request, headers);

        //then
        assertEquals("key", headers.get(AWS2S3Constants.KEY));
        assertEquals(300L, headers.get(AWS2S3Constants.DOWNLOAD_LINK_EXPIRATION_TIME));
        assertEquals("uploader", headers.get("Upload"));
        assertEquals("presigner", headers.get("Presign"));
        assertEquals("description", headers.get("ResourceLinkDescription"));
        assertArrayEquals("BODY".getBytes(), (byte[])actual);
        assertFalse((boolean)headers.get("Failed"));
    }
}
