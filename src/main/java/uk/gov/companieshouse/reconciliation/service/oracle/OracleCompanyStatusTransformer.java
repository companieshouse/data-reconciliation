package uk.gov.companieshouse.reconciliation.service.oracle;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.model.ResultModel;
import uk.gov.companieshouse.reconciliation.model.Results;

/**
 * Transform aggregated company status data fetched from Oracle ResultSets into a {@link Results
 * results} model.
 */
@Component
public class OracleCompanyStatusTransformer {

    /**
     * Transform aggregated company status data fetched from Oracle ResultSets into a {@link Results
     * results} model.
     *
     * The incoming ResultSets will be transformed by Camel into a {@link List list} of {@link Map
     * maps} containing an incorporation number and a company status and aggregated.
     *
     * Each exchange contains a list of companies with the same company status.
     * <pre>
     * List
     *   Exchange
     *     Header
     *       String: company-status
     *
     *     Body
     *       List
     *       Map
     *         key: INCORPORATION_NUMBER
     *         value: 12345678
     *       Map
     *         key: INCORPORATION_NUMBER
     *         value: 12345678
     * </pre>
     *
     * @param exchanges list of exchanges
     * @return A {@link Results results} model containing all {@link ResultModel result models}
     * mapped from the transformed ResultSets.
     */
    public Results transform(@Body List<Exchange> exchanges) {

        return new Results(exchanges.stream().flatMap(this::flatMapToResultModel)
                .collect(Collectors.toList()));
    }

    private Stream<ResultModel> flatMapToResultModel(Exchange exchange) {
        return ((List<?>) exchange.getIn().getBody(List.class)).
                stream().
                map(m -> new ResultModel(
                        Optional.ofNullable((String) ((Map<?, ?>) m).get("INCORPORATION_NUMBER"))
                                .orElse(""),
                        "",
                        Optional.ofNullable(
                                exchange.getIn().getHeader("CompanyStatus", String.class))
                                .orElse("")));
    }
}
