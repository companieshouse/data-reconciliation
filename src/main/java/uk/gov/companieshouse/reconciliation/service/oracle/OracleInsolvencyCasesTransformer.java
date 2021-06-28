package uk.gov.companieshouse.reconciliation.service.oracle;

import org.apache.camel.Body;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.model.InsolvencyResultModel;
import uk.gov.companieshouse.reconciliation.model.InsolvencyResults;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Transforms a data structure representing an SQL ResultSet into a {@link uk.gov.companieshouse.reconciliation.model.InsolvencyResults results object}
 * that can be compared with insolvency details fetched from another data source.<br>
 */
@Component
public class OracleInsolvencyCasesTransformer {

    /**
     * Transform a {@link List} of {@link Map} containing {@link String}-{@link Object} pairings representing individual
     * relations in the SQL ResultSet that has been returned.
     *
     * @param resultSet The ResultSet returned by Oracle.
     * @return A {@link uk.gov.companieshouse.reconciliation.model.InsolvencyResults results object}
     * that can be compared with insolvency details fetched from another data source.
     */
    public InsolvencyResults transform(@Body List<Map<String, Object>> resultSet) {
        return new InsolvencyResults(resultSet.stream()
                .filter(Objects::nonNull)
                .map(result -> new InsolvencyResultModel(
                        Optional.ofNullable(result.get("INCORPORATION_NUMBER")).map(Object::toString).orElse(""),
                        Optional.ofNullable((BigDecimal) result.get("NUMBER_OF_CASES")).map(BigDecimal::intValue).orElse(0))
                ).collect(Collectors.toList()));
    }
}
