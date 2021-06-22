package uk.gov.companieshouse.reconciliation.service.mongo;

import org.apache.camel.Body;
import org.apache.camel.Header;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Transform a {@link java.util.List list of results} obtained via a db.collection.distinct query into a
 * {@link ResourceList resource list} and map the result to the body.
 */
public class MongoDistinctToResourceListTransformer {

    /**
     * Transform a {@link java.util.List list of results} into a {@link ResourceList resource list} excluding null
     * values.
     *
     * @param results A {@link java.util.List list of results} returned from a db.collection.distinct query.
     * @param description A description of the results.
     */
    public ResourceList transform(@Body List<String> results, @Header("Description") String description) {
        Set<String> disqualifications = results.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        return new ResourceList(disqualifications, description);
    }
}
