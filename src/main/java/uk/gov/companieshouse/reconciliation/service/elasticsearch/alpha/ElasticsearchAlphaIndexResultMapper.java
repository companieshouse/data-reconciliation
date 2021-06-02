package uk.gov.companieshouse.reconciliation.service.elasticsearch.alpha;

import org.elasticsearch.search.SearchHit;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.model.ResultModel;
import uk.gov.companieshouse.reconciliation.service.elasticsearch.ElasticsearchResultMappable;

import java.util.Map;
import java.util.Optional;

@Component
public class ElasticsearchAlphaIndexResultMapper implements ElasticsearchResultMappable {

    @Override
    public ResultModel mapWithSourceFields(SearchHit hit) {
        String corporateName = getSourceField(hit, "corporate_name");
        return new ResultModel(hit.getId(), corporateName);
    }

    @Override
    public ResultModel mapExcludingSourceFields(SearchHit hit) {
        return new ResultModel(hit.getId(), "");
    }

    private String getSourceField(SearchHit hit, String sourceField) {
        return Optional.ofNullable(hit.getSourceAsMap().get("items"))
                .map(item -> ((Map<?,?>)item).get(sourceField))
                .map(Object::toString)
                .map(String::trim)
                .filter(nameEnding -> !nameEnding.isEmpty())
                .orElse("");
    }
}
