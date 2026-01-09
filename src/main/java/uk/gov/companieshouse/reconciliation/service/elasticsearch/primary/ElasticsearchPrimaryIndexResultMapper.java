package uk.gov.companieshouse.reconciliation.service.elasticsearch.primary;

import co.elastic.clients.elasticsearch.core.search.Hit;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.model.ResultModel;
import uk.gov.companieshouse.reconciliation.service.elasticsearch.ElasticsearchResultMappable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ElasticsearchPrimaryIndexResultMapper implements ElasticsearchResultMappable {

    @Override
    public ResultModel mapWithSourceFields(Hit<Object> hit) {
        Object src = hit.source();
        if (!(src instanceof Map)) {
            return new ResultModel(hit.id(), "", "");
        }
        List<String> names = new ArrayList<>();
        Map<?,?> sourceMap = (Map<?,?>) src;
        addSourceFieldToNameList(names, sourceMap, "corporate_name_start");
        addSourceFieldToNameList(names, sourceMap, "corporate_name_ending");
        String companyStatus = getFieldValue(sourceMap, "company_status").orElse("");
        return new ResultModel(hit.id(), String.join(" ", names), companyStatus);
    }

    @Override
    public ResultModel mapExcludingSourceFields(Hit<Object> hit) {
        return new ResultModel(hit.id(), "", "");
    }

    private void addSourceFieldToNameList(List<String> names, Map<?,?> sourceMap, String sourceField) {
        getFieldValue(sourceMap, sourceField)
                .filter(nameEnding -> !nameEnding.isEmpty())
                .ifPresent(names::add);
    }

    private Optional<String> getFieldValue(Map<?,?> sourceMap, String sourceField) {
        return Optional.ofNullable(sourceMap.get("items"))
                .flatMap(items -> ((List<?>)items).stream().findFirst())
                .map(item -> ((Map<?,?>)item).get(sourceField))
                .map(Object::toString)
                .map(String::trim);
    }
}
