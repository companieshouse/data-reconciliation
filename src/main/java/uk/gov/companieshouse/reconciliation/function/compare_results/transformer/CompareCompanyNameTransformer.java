package uk.gov.companieshouse.reconciliation.function.compare_results.transformer;

import org.apache.camel.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.function.compare_results.mapper.CompareCompanyNameResultMapper;
import uk.gov.companieshouse.reconciliation.model.Results;

import java.util.List;
import java.util.Map;

@Component
public class CompareCompanyNameTransformer {

    private final CompareFieldsResultsTransformer transformer;
    private final CompareCompanyNameResultMapper mapper;

    @Autowired
    public CompareCompanyNameTransformer(
            CompareFieldsResultsTransformer transformer, CompareCompanyNameResultMapper mapper) {
        this.transformer = transformer;
        this.mapper = mapper;
    }

    public List<Map<String, Object>> transform(@Header("SrcList") Results srcResults,
                                               @Header("SrcDescription") String srcDescription,
                                               @Header("TargetList") Results targetResults,
                                               @Header("TargetDescription") String targetDescription,
                                               @Header("RecordKey") String recordKey) {
        return transformer.transform(srcResults, srcDescription, targetResults, targetDescription, recordKey, mapper);
    }

}
