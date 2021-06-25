package uk.gov.companieshouse.reconciliation.function.compare_results;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.function.compare_results.transformer.CompareInsolvencyCasesTransformer;

/**
 * Compares the number of insolvency cases on the intersection of company numbers in two data sources and returns a
 * data structure suitable for being marshalled into CSV.<br>
 * <br>
 * IN:<br>
 * header(SrcList): An {@link uk.gov.companieshouse.reconciliation.model.InsolvencyResults object} aggregating insolvency
 * data fetched from the first data source.<br>
 * header(SrcDescription): A {@link String plaintext} description of the results fetched from the first data source.<br>
 * header(TargetList): An {@link uk.gov.companieshouse.reconciliation.model.InsolvencyResults object} aggregating insolvency
 * data fetched from the second data source.<br>
 * header(TargetDescription): A {@link String plaintext} description of the results fetched from the second data source.<br>
 * header(RecordKey): A {@link String plaintext} description of the key of each record.<br>
 * <br>
 * OUT:<br>
 * body(): A {@link java.util.List} of {@link java.util.Map} containing {@link String}-{@link Object} pairs representing
 * individual relations in the CSV file.
 */
@Component
public class CompareInsolvencyCasesRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:compare-insolvency-cases")
                .bean(CompareInsolvencyCasesTransformer.class);
    }
}
