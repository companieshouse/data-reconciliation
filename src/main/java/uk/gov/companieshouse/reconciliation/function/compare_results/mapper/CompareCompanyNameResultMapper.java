package uk.gov.companieshouse.reconciliation.function.compare_results.mapper;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.model.ResultModel;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CompareCompanyNameResultMapper implements CompanyResultsMappable {

    @Override
    public Map<String, String> generateMappings(Collection<ResultModel> resultModels) {
        return resultModels.stream().collect(
                Collectors.toMap(ResultModel::getCompanyNumber,
                        resultModel -> Optional.ofNullable(resultModel.getCompanyName()).orElse("")
                ));
    }
}
