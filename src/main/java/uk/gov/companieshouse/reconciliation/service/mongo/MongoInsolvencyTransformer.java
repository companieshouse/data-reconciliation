package uk.gov.companieshouse.reconciliation.service.mongo;

import org.apache.camel.Body;
import org.bson.Document;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.model.InsolvencyResultModel;
import uk.gov.companieshouse.reconciliation.model.InsolvencyResults;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Aggregates insolvencies fetched from MongoDB into a model.
 */
@Component
public class MongoInsolvencyTransformer {

    /**
     * Aggregates {@link Document insolvencies} into a {@link InsolvencyResults results object} comparable
     * with another data source.
     *
     * @param items A collection of {@link Document insolvencies} fetched from MongoDB.
     * @return A {@link InsolvencyResults results object} containing all insolvencies fetched from MongoDB.
     */
    public InsolvencyResults transform(@Body List<Document> items) {
        return new InsolvencyResults(items.stream().map(document -> new InsolvencyResultModel(
                Optional.ofNullable(document.get("_id", String.class)).orElse(""),
                Optional.ofNullable(document.get("cases", Integer.class)).orElse(0))
        ).collect(Collectors.toList()));
    }
}
