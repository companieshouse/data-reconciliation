package uk.gov.companieshouse.reconciliation.service.elasticsearch.alpha;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.search.SearchHit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll.client.ElasticsearchSlicedScrollIterator;
import uk.gov.companieshouse.reconciliation.config.aws.S3ClientConfig;
import uk.gov.companieshouse.reconciliation.model.ResultModel;
import uk.gov.companieshouse.reconciliation.model.Results;

@CamelSpringBootTest
@SpringBootTest
@DirtiesContext
@TestPropertySource(locations = "classpath:application-stubbed.properties")
@ExtendWith(MockitoExtension.class)
@Import(S3ClientConfig.class)
public class ElasticsearchAlphaIndexRouteTest {

    @Autowired
    private CamelContext context;

    @Produce("direct:elasticsearch-alpha")
    private ProducerTemplate producer;

    @Mock
    private ElasticsearchSlicedScrollIterator iterator;

    @Test
    void testTransformAlphaIndexResponseIntoResults() {
        // given
        when(iterator.hasNext()).thenReturn(true, false);
        SearchHit hit = mock(SearchHit.class);
        when(hit.getId()).thenReturn("12345678");
        when(hit.getSourceAsMap()).thenReturn(java.util.Map.of(
                "items", java.util.Map.of(
                        "corporate_name", "ACME LIMITED",
                        "company_status", ""
                )
        ));
        when(hit.hasSource()).thenReturn(true);
        when(iterator.next()).thenReturn(hit);
        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setBody(iterator);

        // when
        Exchange actual = producer.send(exchange);

        // then
        assertTrue(actual.getIn().getBody(Results.class).contains(new ResultModel("12345678", "ACME LIMITED", "")));
        verify(iterator, times(2)).hasNext();
        verify(iterator, times(1)).next();
    }
}
