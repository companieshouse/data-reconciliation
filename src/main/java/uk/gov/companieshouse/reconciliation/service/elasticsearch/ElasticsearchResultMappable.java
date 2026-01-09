package uk.gov.companieshouse.reconciliation.service.elasticsearch;

import co.elastic.clients.elasticsearch.core.search.Hit;
import uk.gov.companieshouse.reconciliation.model.ResultModel;

/**
 * Maps fields from a single {@link Hit search hit} returned by Elasticsearch to a
 * {@link ResultModel result model}.
 */
public interface ElasticsearchResultMappable {
    /**
     * Map ID and source fields of a {@link Hit search hit} to a {@link ResultModel result model}.
     *
     * @param hit A single {@link Hit search hit} returned by Elasticsearch.
     * @return A {@link ResultModel result model} mapped from the {@link Hit search hit object}.
     */
    ResultModel mapWithSourceFields(Hit<Object> hit);

    /**
     * Map a {@link Hit search hit} excluding source fields to a {@link ResultModel result model}.
     *
     * @param hit A single {@link Hit search hit} returned by Elasticsearch.
     * @return A {@link ResultModel result model} mapped from the {@link Hit search hit object}.
     */
    ResultModel mapExcludingSourceFields(Hit<Object> hit);
}
