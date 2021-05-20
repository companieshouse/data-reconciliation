package uk.gov.companieshouse.reconciliation.service.elasticsearch;

import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll.client.ElasticsearchSlicedScrollIterator;
import uk.gov.companieshouse.reconciliation.model.ResultModel;
import uk.gov.companieshouse.reconciliation.model.Results;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ElasticsearchTransformerTest {

    @Mock
    private ElasticsearchSlicedScrollIterator iterator;

    private ElasticsearchTransformer transformer;

    @BeforeEach
    void setUp() {
        transformer = new ElasticsearchTransformer();
    }

    @Test
    void testAggregateSearchHitsIntoResourceList() {
        //given
        when(iterator.hasNext()).thenReturn(true, false);
        String source = "{ \"corporate_name_start\": \"ACME\", \"corporate_name_end\": \" LIMITED\" }";
        SearchHit hit = new SearchHit(123, "12345678", new Text("{}"), Collections.emptyMap());
        hit.sourceRef(new BytesArray(source));
        when(iterator.next()).thenReturn(hit);

        //when
        Results actual = transformer.transform(iterator, 1);

        //then
        verify(iterator, times(2)).hasNext();
        verify(iterator, times(1)).next();
        assertTrue(actual.contains(new ResultModel("12345678", "ACME LIMITED")));
    }
}
