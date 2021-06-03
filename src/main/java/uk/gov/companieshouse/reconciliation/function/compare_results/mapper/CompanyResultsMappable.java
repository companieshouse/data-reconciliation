package uk.gov.companieshouse.reconciliation.function.compare_results.mapper;

import uk.gov.companieshouse.reconciliation.model.ResultModel;

import java.util.Collection;
import java.util.Map;

public interface CompanyResultsMappable {
    Map<String, String> generateMappings(Collection<ResultModel> resultModels);
}
