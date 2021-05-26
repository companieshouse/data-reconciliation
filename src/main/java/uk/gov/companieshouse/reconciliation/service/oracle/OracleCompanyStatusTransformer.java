package uk.gov.companieshouse.reconciliation.service.oracle;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.camel.Body;
import org.apache.camel.Header;
import org.apache.camel.Headers;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.model.ResultModel;
import uk.gov.companieshouse.reconciliation.model.Results;

@Component
public class OracleCompanyStatusTransformer {

    public Results transform(@Body List<List<Map<String, Object>>> resultSet) {

        return new Results(resultSet.stream()
                .flatMap(Collection::stream)
                .map(map -> new ResultModel(
                        Optional.ofNullable((String) map.get("incorporation_number")).orElse(""),
                        "",
                        Optional.ofNullable((String) map.get("company_status")).orElse("")))
                .collect(Collectors.toList()));
    }
}
