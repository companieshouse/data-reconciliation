package uk.gov.companieshouse.reconciliation.config;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Queries used to retrieve data from MongoDB.
 */
@Configuration
public class MongoDBQueryConfiguration {

    /**
     * @return An aggregation pipeline used to fetch officer_id_raw fields from a collection of disqualified officers.
     */
    @Bean
    List<Bson> disqualifiedOfficerAggregationQuery() {
        return Collections.singletonList(Aggregates.project(Projections.fields(Projections.include("officer_id_raw"))));
    }

    /**
     * @return An aggregation pipeline used to fetch insolvency case counts from a collection of insolvencies.
     */
    @Bean
    List<Bson> insolvencyAggregationQuery() {
        return Arrays.asList(
                Aggregates.match(Filters.exists("data.cases.number")),
                Aggregates.project(Projections.fields(
                        Projections.include("_id"),
                        Projections.computed("cases", new Document("$size", "$data.cases.number")))));
    }
}
