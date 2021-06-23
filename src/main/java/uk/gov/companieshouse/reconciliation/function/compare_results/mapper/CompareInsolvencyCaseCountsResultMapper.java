package uk.gov.companieshouse.reconciliation.function.compare_results.mapper;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.model.InsolvencyResultModel;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CompareInsolvencyCaseCountsResultMapper implements CompanyResultsMappable<InsolvencyResultModel> {

    @Override
    public Map<String, String> generateMappings(Collection<InsolvencyResultModel> resultModels) {
        return resultModels.stream()
                .collect(Collectors.toMap(InsolvencyResultModel::getCompanyNumber,
                        (insolvencyResultModel) -> Integer.toString(insolvencyResultModel.getInsolvencyCases())));
    }
}
