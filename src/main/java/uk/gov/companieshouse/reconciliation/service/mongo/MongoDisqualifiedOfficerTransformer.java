package uk.gov.companieshouse.reconciliation.service.mongo;

import org.apache.camel.Body;
import org.bson.Document;
import uk.gov.companieshouse.reconciliation.model.DisqualificationResultModel;
import uk.gov.companieshouse.reconciliation.model.DisqualificationResults;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Aggregate disqualifications fetched from MongoDB into a {@link DisqualificationResults results object}.
 */
public class MongoDisqualifiedOfficerTransformer {

    /**
     * Aggregate disqualifications fetched from MongoDB into a {@link DisqualificationResults results object}.
     *
     * @param disqualifications A {@link java.util.List list} of {@link Document documents} returned by the query run
     *                          against the disqualifications collection in MongoDB.
     * @return A {@link DisqualificationResults results object} aggregating all disqualifications fetched from MongoDB.
     */

    public DisqualificationResults transform(@Body List<Document> disqualifications) {
        return new DisqualificationResults(disqualifications.stream()
                .filter(Objects::nonNull)
                .map(elem -> new DisqualificationResultModel(Optional.ofNullable(elem.get("officer_id_raw", String.class)).orElse("")))
                .collect(Collectors.toList()));
    }
}
