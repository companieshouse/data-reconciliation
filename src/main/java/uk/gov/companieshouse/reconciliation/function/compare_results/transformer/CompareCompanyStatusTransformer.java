package uk.gov.companieshouse.reconciliation.function.compare_results.transformer;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.camel.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.model.ResultModel;
import uk.gov.companieshouse.reconciliation.model.Results;

@Component
public class CompareCompanyStatusTransformer {
    @Autowired
    private CompareFieldsResultsTransformer transformer;

    public List<Map<String, Object>> transform(@Header("SrcList") Results srcResults,
            @Header("SrcDescription") String srcDescription,
            @Header("TargetList") Results targetResults,
            @Header("TargetDescription") String targetDescription,
            @Header("RecordType") String recordType) {
        return transformer.transform(srcResults, srcDescription, targetResults, targetDescription, recordType, this::generateMappings);
    }
    private Map<String, String> generateMappings(Collection<ResultModel> resultModels) {
        return resultModels.stream().collect(
                Collectors.toMap(ResultModel::getCompanyNumber, ResultModel::getCompanyStatus));
    }
}
