package uk.gov.companieshouse.reconciliation.service.mongo;

import org.apache.camel.Body;
import org.apache.camel.Header;
import org.apache.camel.Headers;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Transform a {@link java.util.List list of results} obtained via a db.collection.distinct query into a
 * {@link ResourceList resource list} and map the result to the header specified by header(MongoTargetHeader).
 */
public class MongoDistinctToResourceListTransformer {

    /**
     * Transform a {@link java.util.List list of results} into a {@link ResourceList resource list} excluding null
     * values.
     *
     * @param results A {@link java.util.List list of results} returned from a db.collection.distinct query.
     * @param description A description of the results.
     * @param targetHeader The header to which the {@link ResourceList resource list} will be mapped.
     * @param headers {@link java.util.Map Name-value pairings} representing incoming headers.
     */
    public void transform(@Body List<String> results, @Header("Description") String description,
                          @Header("MongoTargetHeader") String targetHeader, @Headers Map<String, Object> headers) {
        Set<String> disqualifications = results.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        headers.put(targetHeader, new ResourceList(disqualifications, description));
    }
}
