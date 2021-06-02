package uk.gov.companieshouse.reconciliation.service.elasticsearch;

import org.elasticsearch.search.SearchHit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll.client.ElasticsearchSlicedScrollIterator;
import uk.gov.companieshouse.reconciliation.model.ResultModel;
import uk.gov.companieshouse.reconciliation.model.Results;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ElasticsearchTransformerTest {

    @Mock
    private SearchHit searchHit;

    @Mock
    private ElasticsearchSlicedScrollIterator iterator;

    @Mock
    private ResultModel resultModel;

    @Mock
    private ElasticsearchResultMappable mappingFunction;

    private ElasticsearchTransformer transformer;

    @BeforeEach
    void setUp() {
        transformer = new ElasticsearchTransformer(1);
    }

    @Test
    void testReturnEmptyResultsObjectIfNoHitsReturned() {
        //given
        when(iterator.hasNext()).thenReturn(false);

        //when
        Results actual = transformer.transform(iterator, 1, mappingFunction);

        //then
        assertEquals(0, actual.size());
        verifyNoInteractions(mappingFunction);
    }

    @Test
    void testReturnResultsSourceFieldsIncluded() {
        //given
        when(iterator.hasNext()).thenReturn(true, false);
        when(iterator.next()).thenReturn(searchHit);
        when(searchHit.hasSource()).thenReturn(true);
        when(mappingFunction.mapWithSourceFields(any())).thenReturn(resultModel);

        //when
        Results actual = transformer.transform(iterator, 1, mappingFunction);

        //then
        assertSame(resultModel, actual.getResultModels().iterator().next());
        verify(mappingFunction).mapWithSourceFields(searchHit);
    }

    @Test
    void testReturnResultsSourceFieldsExcluded() {
        //given
        when(iterator.hasNext()).thenReturn(true, false);
        when(iterator.next()).thenReturn(searchHit);
        when(searchHit.hasSource()).thenReturn(false);
        when(mappingFunction.mapExcludingSourceFields(any())).thenReturn(resultModel);

        //when
        Results actual = transformer.transform(iterator, 1, mappingFunction);

        //then
        assertSame(resultModel, actual.getResultModels().iterator().next());
        verify(mappingFunction).mapExcludingSourceFields(searchHit);
    }
}
