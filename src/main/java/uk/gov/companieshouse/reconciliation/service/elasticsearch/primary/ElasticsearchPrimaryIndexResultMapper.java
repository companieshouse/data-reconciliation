package uk.gov.companieshouse.reconciliation.service.elasticsearch.primary;

import org.elasticsearch.search.SearchHit;
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
    public ResultModel mapWithSourceFields(SearchHit hit) {
        List<String> names = new ArrayList<>();
        addSourceFieldToNameList(names, hit, "corporate_name_start");
        addSourceFieldToNameList(names, hit, "corporate_name_ending");
        return new ResultModel(hit.getId(), String.join(" ", names));
    }

    @Override
    public ResultModel mapExcludingSourceFields(SearchHit hit) {
        return new ResultModel(hit.getId(), "");
    }

    private void addSourceFieldToNameList(List<String> names, SearchHit hit, String sourceField) {
        Optional.ofNullable(hit.getSourceAsMap().get("items"))
                .flatMap(items -> ((List<?>)items).stream().findFirst())
                .map(item -> ((Map<?,?>)item).get(sourceField))
                .map(Object::toString)
                .map(String::trim)
                .filter(nameEnding -> !nameEnding.isEmpty())
                .ifPresent(names::add);
    }

}
