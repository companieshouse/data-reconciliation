package uk.gov.companieshouse.reconciliation.service.mongo;

import org.apache.camel.Body;
import org.apache.camel.Header;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;
import uk.gov.companieshouse.reconciliation.model.DisqualificationResultModel;
import uk.gov.companieshouse.reconciliation.model.DisqualificationResults;

import java.util.stream.Collectors;

/**
 * Transform {@link DisqualificationResults results} into a {@link ResourceList list} containing raw officer IDs.
 */
public class MongoDisqualifiedOfficerResultsToResourceListTransformer {

    /**
     * Transform {@link DisqualificationResults results} into a {@link ResourceList list} containing raw officer IDs.
     * @param results A {@link DisqualificationResults collection} of disqualified officers.
     * @param description A description of the collection of resources.
     * @return A {@link ResourceList resource list} containing disqualified officer IDs.
     */
    public ResourceList transform(@Body DisqualificationResults results, @Header("Description") String description) {
        return new ResourceList(results.getResultModels()
                .stream()
                .map(DisqualificationResultModel::getOfficerIdRaw)
                .collect(Collectors.toList()), description);
    }
}
