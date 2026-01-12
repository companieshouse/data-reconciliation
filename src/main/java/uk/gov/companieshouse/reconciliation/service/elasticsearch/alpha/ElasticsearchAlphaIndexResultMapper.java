package uk.gov.companieshouse.reconciliation.service.elasticsearch.alpha;

import co.elastic.clients.elasticsearch.core.search.Hit;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.model.ResultModel;
import uk.gov.companieshouse.reconciliation.service.elasticsearch.ElasticsearchResultMappable;

import java.util.Map;
import java.util.Optional;

@Component
public class ElasticsearchAlphaIndexResultMapper implements ElasticsearchResultMappable {

    @Override
    public ResultModel mapWithSourceFields(Hit<Object> hit) {
        Object src = hit.source();
        if (!(src instanceof Map)) {
            return new ResultModel(hit.id(), "", "");
        }
        Map<?,?> sourceMap = (Map<?,?>) src;
        String corporateName = getSourceField(sourceMap, "corporate_name");
        String companyStatus = getSourceField(sourceMap, "company_status");
        return new ResultModel(hit.id(), corporateName, companyStatus);
    }

    @Override
    public ResultModel mapExcludingSourceFields(Hit<Object> hit) {
        return new ResultModel(hit.id(), "");
    }

    private String getSourceField(Map<?,?> sourceMap, String sourceField) {
        Object itemsObj = sourceMap.get("items");
        if (!(itemsObj instanceof Map)) {
            return "";
        }
        Object value = ((Map<?,?>)itemsObj).get(sourceField);
        if (value == null) return "";
        String str = value == null ? "" : value.toString().trim();
        return str.isEmpty() ? "" : str;
    }
}
