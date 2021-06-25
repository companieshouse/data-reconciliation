package uk.gov.companieshouse.reconciliation.service.mongo;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Transforms a collection of {@link org.bson.Document company profile documents} into a
 * {@link uk.gov.companieshouse.reconciliation.model.Results results object} comparable with
 * {@link uk.gov.companieshouse.reconciliation.model.Results results} fetched from another data source.
 */
@Component
public class MongoCompanyProfileTransformerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:company-profile-transformer")
                .bean(MongoCompanyProfileTransformer.class);
    }
}
