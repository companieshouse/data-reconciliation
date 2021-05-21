package uk.gov.companieshouse.reconciliation.service.transformer;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;
import uk.gov.companieshouse.reconciliation.model.ResultModel;
import uk.gov.companieshouse.reconciliation.model.Results;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ResultsToCompanyNumberTransformer {

    public void transform(Results resultModels, String description, String targetHeader, Map<String, Object> headers) {
        Set<String> companyNumbers = resultModels.getResultModels().stream()
                .map(ResultModel::getCompanyNumber)
                .collect(Collectors.toSet());
        ResourceList resourceList = new ResourceList(companyNumbers, description);
        headers.put(targetHeader, resourceList);
    }
}
