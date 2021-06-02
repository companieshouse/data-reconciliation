package uk.gov.companieshouse.reconciliation.service.elasticsearch.primary;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll.client.ElasticsearchSlicedScrollIterator;
import uk.gov.companieshouse.reconciliation.model.Results;
import uk.gov.companieshouse.reconciliation.service.elasticsearch.ElasticsearchTransformer;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ElasticsearchPrimaryIndexTransformerTest {

    @Mock
    private ElasticsearchSlicedScrollIterator iterator;

    @Mock
    private ElasticsearchTransformer resultTransformer;

    @Mock
    private Results results;

    @Mock
    private ElasticsearchPrimaryIndexResultMapper searchHitMapper;

    private ElasticsearchPrimaryIndexTransformer transformer;

    @BeforeEach
    void setUp() {
        transformer = new ElasticsearchPrimaryIndexTransformer(resultTransformer, searchHitMapper);
    }

    @Test
    void testTransformSearchHits() {
        //given
        when(resultTransformer.transform(any(), any(), any())).thenReturn(results);

        //when
        Results actual = transformer.transform(iterator, 1);

        //then
        assertSame(results, actual);
    }
}
