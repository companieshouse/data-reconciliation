package uk.gov.companieshouse.reconciliation.email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.reconciliation.function.email.EmailPublisherSplitter;
import uk.gov.companieshouse.reconciliation.function.email.PublisherResourceRequest;
import uk.gov.companieshouse.reconciliation.function.email.PublisherResourceRequestWrapper;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmailPublisherSplitterTest {

    private EmailPublisherSplitter splitter;

    @BeforeEach
    void setUp() {
        this.splitter = new EmailPublisherSplitter();
    }

    @Test
    void testReturnListOfPublisherResourceRequests() {
        //given
        PublisherResourceRequestWrapper resourceRequestWrapper = new PublisherResourceRequestWrapper(Collections.singletonList(new PublisherResourceRequest("key", 300, "uploader", "presigner", "description", "BODY".getBytes(), "group", "AggregationModelId", true, "description")));

        //when
        List<PublisherResourceRequest> actual = splitter.split(resourceRequestWrapper);

        //then
        assertEquals(Collections.singletonList(new PublisherResourceRequest("key", 300, "uploader", "presigner", "description", "BODY".getBytes(), "group", "AggregationModelId", true, "description")), actual);
    }
}
