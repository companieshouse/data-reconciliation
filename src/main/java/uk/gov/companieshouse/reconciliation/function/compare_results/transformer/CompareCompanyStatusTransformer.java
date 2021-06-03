package uk.gov.companieshouse.reconciliation.function.compare_results.transformer;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.camel.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.function.compare_results.mapper.CompareCompanyNameResultMapper;
import uk.gov.companieshouse.reconciliation.model.ResultModel;
import uk.gov.companieshouse.reconciliation.model.Results;

@Component
public class CompareCompanyStatusTransformer {

    private final CompareFieldsResultsTransformer transformer;
    private final CompareCompanyNameResultMapper mapper;

    @Autowired
    public CompareCompanyStatusTransformer(CompareFieldsResultsTransformer transformer, CompareCompanyNameResultMapper mapper) {
        this.transformer = transformer;
        this.mapper = mapper;
    }

    public List<Map<String, Object>> transform(@Header("SrcList") Results srcResults,
            @Header("SrcDescription") String srcDescription,
            @Header("TargetList") Results targetResults,
            @Header("TargetDescription") String targetDescription,
            @Header("RecordKey") String recordKey) {
        return transformer.transform(srcResults, srcDescription, targetResults, targetDescription, recordKey, mapper::generateMappings);
    }
    /*
     @Override
    public Map<String, String> generateMappings(Collection<ResultModel> resultModels) {
        return resultModels.stream().collect(
                Collectors.toMap(ResultModel::getCompanyNumber, ResultModel::getCompanyStatus));
    }
     */
}
