package uk.gov.companieshouse.reconciliation.service.mongo;

import org.apache.camel.Body;
import org.bson.Document;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.model.InsolvencyResultModel;
import uk.gov.companieshouse.reconciliation.model.InsolvencyResults;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MongoInsolvencyTransformer {

    public InsolvencyResults transform(@Body List<Document> items) {
        return new InsolvencyResults(items.stream().map(document -> new InsolvencyResultModel(
                Optional.ofNullable(document.get("_id", String.class)).orElse(""),
                Optional.ofNullable(document.get("cases", Integer.class)).orElse(0))
        ).collect(Collectors.toList()));
    }
}
