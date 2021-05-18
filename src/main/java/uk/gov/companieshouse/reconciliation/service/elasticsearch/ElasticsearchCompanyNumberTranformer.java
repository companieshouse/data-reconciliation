package uk.gov.companieshouse.reconciliation.service.elasticsearch;

import org.apache.camel.Body;
import org.apache.camel.Header;
import org.apache.camel.Headers;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;
import uk.gov.companieshouse.reconciliation.model.ResultModel;
import uk.gov.companieshouse.reconciliation.model.Results;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ElasticsearchCompanyNumberTranformer {

    public void transform(@Body Results resultModels, @Header("ElasticsearchDescription") String description,
                                  @Header("ElasticsearchTargetHeader") String elasticsearchTargetHeader, @Headers Map<String, Object> headers) {
        Set<String> companyNumbers = resultModels.getResultModels().stream()
                .map(ResultModel::getCompanyNumber)
                .collect(Collectors.toSet());
        ResourceList resourceList = new ResourceList(companyNumbers, description);
        headers.put(elasticsearchTargetHeader, resourceList);
    }
}
