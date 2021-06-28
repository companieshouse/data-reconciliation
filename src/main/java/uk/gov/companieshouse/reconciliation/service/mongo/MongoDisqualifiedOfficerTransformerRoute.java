package uk.gov.companieshouse.reconciliation.service.mongo;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Transforms a collection of {@link org.bson.Document disqualification documents} into a
 * {@link uk.gov.companieshouse.reconciliation.model.DisqualificationResults results object} comparable with
 * {@link uk.gov.companieshouse.reconciliation.model.DisqualificationResults results} fetched from another data source.
 */
@Component
public class MongoDisqualifiedOfficerTransformerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:disqualified-officer-transformer")
                .bean(MongoDisqualifiedOfficerTransformer.class);
    }
}
