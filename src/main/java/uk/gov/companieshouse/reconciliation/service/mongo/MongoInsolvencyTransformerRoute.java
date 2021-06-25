package uk.gov.companieshouse.reconciliation.service.mongo;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Transforms a collection of {@link org.bson.Document company profile documents} into a
 * {@link uk.gov.companieshouse.reconciliation.model.InsolvencyResults results object} comparable with
 * {@link uk.gov.companieshouse.reconciliation.model.InsolvencyResults results} fetched from another data source.
 */
@Component
public class MongoInsolvencyTransformerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:mongo-insolvency_cases-transformer")
                .bean(MongoInsolvencyTransformer.class);
    }
}
