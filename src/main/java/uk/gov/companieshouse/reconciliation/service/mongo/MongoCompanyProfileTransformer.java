package uk.gov.companieshouse.reconciliation.service.mongo;

import org.apache.camel.Body;
import org.bson.Document;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.model.ResultModel;
import uk.gov.companieshouse.reconciliation.model.Results;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Aggregate company profiles fetched from MongoDB into a {@link Results results object}.
 */
@Component
public class MongoCompanyProfileTransformer {

    /**
     * Aggregate company profiles fetched from MongoDB into a {@link Results results object}.
     *
     * @param items A {@link java.util.List list} of {@link Document documents} returned by the query run against the
     *              company profile collection in MongoDB.
     * @return A {@link Results results object} aggregating all company profiles fetched from MongoDB.
     */
    public Results transform(@Body List<Document> items) {
        List<ResultModel> results = items.stream()
                .map(item -> {
                    String id = Optional.ofNullable(item.get("_id"))
                            .map(Object::toString).orElse("");
                    String companyName = Optional.ofNullable((Document)item.get("data"))
                            .map(data -> data.get("company_name"))
                            .map(Object::toString).orElse("");
                    String companyStatus = Optional.ofNullable((Document)item.get("data"))
                            .map(data -> data.get("company_status"))
                            .map(Object::toString).orElse("");
                    return new ResultModel(id, companyName, companyStatus);
                })
                .collect(Collectors.toList());

        return new Results(results);
    }
}
