package uk.gov.companieshouse.reconciliation.service.elasticsearch;

import org.elasticsearch.search.SearchHit;
import uk.gov.companieshouse.reconciliation.model.ResultModel;

/**
 * Maps fields from a single {@link SearchHit search hit} returned by Elasticsearch to a
 * {@link ResultModel result model}.
 */
public interface ElasticsearchResultMappable {
    /**
     * Map ID and source fields of a {@link SearchHit search hit} to a {@link ResultModel result model}.
     *
     * @param hit A single {@link SearchHit search hit} returned by Elasticsearch.
     * @return A {@link ResultModel result model} mapped from the {@link SearchHit search hit object}.
     */
    ResultModel mapWithSourceFields(SearchHit hit);

    /**
     * Map a {@link SearchHit search hit} excluding source fields to a {@link ResultModel result model}.
     *
     * @param hit A single {@link SearchHit search hit} returned by Elasticsearch.
     * @return A {@link ResultModel result model} mapped from the {@link SearchHit search hit object}.
     */
    ResultModel mapExcludingSourceFields(SearchHit hit);
}
