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

@Component
public class OracleInsolvencyCasesTransformer {

    public InsolvencyResults transform(@Body List<Map<String, Object>> resultSet) {
        return new InsolvencyResults(resultSet.stream()
                .filter(Objects::nonNull)
                .map(result -> new InsolvencyResultModel(
                        Optional.ofNullable(result.get("INCORPORATION_NUMBER")).map(Object::toString).orElse(""),
                        Optional.ofNullable((BigDecimal) result.get("NUMBER_OF_CASES")).map(BigDecimal::intValue).orElse(0))
                ).collect(Collectors.toList()));
    }
}
