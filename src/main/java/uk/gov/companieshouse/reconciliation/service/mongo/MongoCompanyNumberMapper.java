package uk.gov.companieshouse.reconciliation.service.mongo;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Retrieve company profiles from MongoDB and transform results into a {@link uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList resource list}.<br>
 * <br>
 * IN:<br>
 * <br>
 * header(MongoDescription): A description of the results produced by this pipeline.<br>
 * header(MongoTargetHeader): The target header to which results will be mapped.<br>
 * <br>
 * OUT:<br>
 * <br>
 * *header(MongoTargetHeader): Company numbers of company profiles fetched from MongoDB.<br>
 */
@Component
public class MongoCompanyNumberMapper extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:mongo-company_number-mapper")
                .to("{{endpoint.mongodb.wrapper.company_profile.collection}}")
                .bean(MongoCompanyNumberTransformer.class);
    }
}
