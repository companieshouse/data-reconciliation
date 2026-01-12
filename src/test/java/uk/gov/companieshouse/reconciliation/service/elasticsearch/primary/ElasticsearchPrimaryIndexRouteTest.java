package uk.gov.companieshouse.reconciliation.service.elasticsearch.primary;

import co.elastic.clients.elasticsearch.core.search.Hit;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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
@Import(S3ClientConfig.class)
class ElasticsearchPrimaryIndexRouteTest {

    @Autowired
    private CamelContext context;

    @Produce("direct:elasticsearch-primary")
    private ProducerTemplate producer;

    @Mock
    private ElasticsearchSlicedScrollIterator iterator;

    @Test
    void testTransformAlphaIndexResponseIntoResults() {
        // given
        when(iterator.hasNext()).thenReturn(true, false);
        Hit<Object> hit = Hit.of(b -> b.id("12345678").index("test-index").source(Map.of(
                "items", List.of(
                        Map.of(
                                "corporate_name_start", "ACME",
                                "corporate_name_ending", " LIMITED"
                        )
                )
        )));
        when(iterator.next()).thenReturn(hit);
        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setBody(iterator);

        // when
        Exchange actual = producer.send(exchange);

        // then
        assertTrue(actual.getIn().getBody(Results.class).contains(new ResultModel("12345678", "ACME LIMITED")));
        verify(iterator, times(2)).hasNext();
        verify(iterator, times(1)).next();
    }
}
