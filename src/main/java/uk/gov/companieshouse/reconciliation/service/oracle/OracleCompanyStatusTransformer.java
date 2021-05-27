package uk.gov.companieshouse.reconciliation.service.oracle;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.camel.Body;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.model.ResultModel;
import uk.gov.companieshouse.reconciliation.model.Results;

/**
 * Transform aggregated company status data fetched from Oracle ResultSets into a {@link Results results} model.
 */
@Component
public class OracleCompanyStatusTransformer {

    /**
     * Transform aggregated company status data fetched from Oracle ResultSets into a {@link Results results} model.
     *
     * The incoming ResultSets will be transformed by Camel into a {@link List list} of {@link Map maps} containing
     * an incorporation number and a company status and aggregated.
     *
     * @param resultSet Aggregated ResultSets returned by Oracle and transformed by Camel.
     * @return A {@link Results results} model containing all {@link ResultModel result models} mapped from the transformed
     * ResultSets.
     */
    public Results transform(@Body List<List<Map<String, Object>>> resultSet) {

        return new Results(resultSet.stream()
                .flatMap(Collection::stream)
                .map(map -> new ResultModel(
                        Optional.ofNullable((String) map.get("INCORPORATION_NUMBER")).orElse(""),
                        "",
                        Optional.ofNullable((String) map.get("COMPANY_STATUS")).orElse("")))
                .collect(Collectors.toList()));
    }
}
