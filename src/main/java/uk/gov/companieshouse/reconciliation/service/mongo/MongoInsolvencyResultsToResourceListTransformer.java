package uk.gov.companieshouse.reconciliation.service.mongo;

import org.apache.camel.Body;
import org.apache.camel.Header;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;
import uk.gov.companieshouse.reconciliation.model.InsolvencyResultModel;
import uk.gov.companieshouse.reconciliation.model.InsolvencyResults;

import java.util.stream.Collectors;

/**
 * Transform {@link InsolvencyResults results} into a {@link ResourceList list} containing company numbers.
 */
public class MongoInsolvencyResultsToResourceListTransformer {

    /**
     * Transform {@link InsolvencyResults results} into a {@link ResourceList list} containing company numbers.
     * @param results A {@link InsolvencyResults collection} of insolvencies.
     * @param description A description of the collection of resources.
     * @return A {@link ResourceList resource list} containing company numbers.
     */
    public ResourceList transform(@Body InsolvencyResults results, @Header("Description") String description) {
        return new ResourceList(results.getResultModels()
                .stream()
                .map(InsolvencyResultModel::getCompanyNumber)
                .collect(Collectors.toList()), description);
    }
}
