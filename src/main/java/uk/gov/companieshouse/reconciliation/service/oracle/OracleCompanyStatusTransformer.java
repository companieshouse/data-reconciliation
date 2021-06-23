package uk.gov.companieshouse.reconciliation.service.oracle;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.model.ResultModel;
import uk.gov.companieshouse.reconciliation.model.ResultModel.Builder;
import uk.gov.companieshouse.reconciliation.model.Results;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
     * validCompanies contains a list of valid companies on CHS.
     * <pre>
     * List
     *   Map
     *     key: INCORPORATION_NUMBER
     *     value: 12345678
     *   Map
     *     key: INCORPORATION_NUMBER
     *     value: 12345678
     * </pre>
     *
     * @param validCompanies companies that are valid in CHS
     * @param statusDecorators list of exchanges
     * @return A {@link Results results} model containing all {@link ResultModel result models}
     * mapped from the transformed ResultSets.
     */
    public Results transform(@Header("ValidCompanies") List<Map<String, Object>> validCompanies, @Body List<Exchange> statusDecorators) {

        final Map<String, ResultModel.Builder> validCompanyMap = validCompanies.stream().flatMap(m->m.entrySet().stream()).collect(Collectors.toMap(e->(String)e.getValue(), this::getBuilder));

        // Decorate valid companies with company status'
        for (Exchange statusDecorator : statusDecorators) {
            String companyStatus = Optional.ofNullable(
                    statusDecorator.getIn().getHeader("CompanyStatus", String.class))
                    .orElse("");
            for (Object mapObject : statusDecorator.getIn().getBody(List.class)) {
                Map<?, ?> map = (Map<?, ?>)mapObject;
                String companyNumber = (String)map.get("INCORPORATION_NUMBER");
                if (companyNumber != null) {
                    ResultModel.Builder builder = validCompanyMap.get(companyNumber);
                    if (builder != null) {
                        builder.withCompanyStatus(companyStatus);
                    }
                }
            }
        }

        return new Results(validCompanyMap.values().stream().map(Builder::build).collect(Collectors.toList()));
    }

    private ResultModel.Builder getBuilder(Map.Entry<String, Object> entry) {
        ResultModel.Builder builder = ResultModel.builder();
        builder.withCompanyNumber((String)entry.getValue());
        builder.withCompanyName("");
        builder.withCompanyStatus("active");
        return builder;
    }
}
