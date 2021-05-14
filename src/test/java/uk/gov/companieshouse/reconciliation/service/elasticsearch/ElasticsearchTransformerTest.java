package uk.gov.companieshouse.reconciliation.service.elasticsearch;

import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll.client.ElasticsearchSlicedScrollIterator;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        Map<String, Object> headers = new HashMap<>();
        when(iterator.hasNext()).thenReturn(true, false);
        when(iterator.next()).thenReturn(new SearchHit(123, "12345678", new Text("{}"), new HashMap<>()));

        //when
        transformer.transform(iterator, "Description", "Target", 1, headers);
        ResourceList actual = (ResourceList)headers.get("Target");

        //then
        verify(iterator, times(2)).hasNext();
        verify(iterator, times(1)).next();
        assertEquals("Description", actual.getResultDesc());
        assertTrue(actual.contains("12345678"));
    }
}
