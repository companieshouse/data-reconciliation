package uk.gov.companieshouse.reconciliation.service.mongo;

import org.apache.camel.Body;
import org.bson.Document;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.model.ResultModel;
import uk.gov.companieshouse.reconciliation.model.Results;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MongoAggregationTransformer {

    public Results transform(@Body List<Document> items) {
        List<ResultModel> results = items.stream()
                .map(item -> new ResultModel(item.getString("_id"), item.get("data", Document.class).getString("company_name")))
                .collect(Collectors.toList());

        return new Results(results);
    }
}
