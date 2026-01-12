package uk.gov.companieshouse.reconciliation.service.elasticsearch;

import co.elastic.clients.elasticsearch.core.search.Hit;
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
class ElasticsearchTransformerTest {

    @Mock
    private Hit<Object> searchHit;

    @Mock
    private ElasticsearchSlicedScrollIterator iterator;

    @Mock
    private ResultModel resultModel;

    @Mock
    private ElasticsearchResultMappable mappingFunction;

    private ElasticsearchTransformer transformer;

    @BeforeEach
    void setUp() {
        transformer = new ElasticsearchTransformer();
    }

    @Test
    void testReturnEmptyResultsObjectIfNoHitsReturned() {
        //given
        when(iterator.hasNext()).thenReturn(false);
        //when
        Results actual = transformer.transform(iterator, mappingFunction, true);
        //then
        assertEquals(0, actual.size());
        verifyNoInteractions(mappingFunction);
    }

    @Test
    void testReturnResultsSourceFieldsIncluded() {
        //given
        when(iterator.hasNext()).thenReturn(true, false);
        when(iterator.next()).thenReturn(searchHit);
        when(mappingFunction.mapWithSourceFields(any())).thenReturn(resultModel);
        //when
        Results actual = transformer.transform(iterator, mappingFunction, true);
        //then
        assertSame(resultModel, actual.getResultModels().iterator().next());
        verify(mappingFunction).mapWithSourceFields(searchHit);
    }

    @Test
    void testReturnResultsSourceFieldsExcluded() {
        //given
        when(iterator.hasNext()).thenReturn(true, false);
        when(iterator.next()).thenReturn(searchHit);
        when(mappingFunction.mapExcludingSourceFields(any())).thenReturn(resultModel);
        //when
        Results actual = transformer.transform(iterator, mappingFunction, false);
        //then
        assertSame(resultModel, actual.getResultModels().iterator().next());
        verify(mappingFunction).mapExcludingSourceFields(searchHit);
    }
}
