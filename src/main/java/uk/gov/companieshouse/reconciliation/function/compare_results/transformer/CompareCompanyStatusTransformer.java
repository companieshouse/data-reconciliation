package uk.gov.companieshouse.reconciliation.function.compare_results.transformer;

import org.apache.camel.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.function.compare_results.mapper.CompareCompanyStatusResultMapper;
import uk.gov.companieshouse.reconciliation.model.Results;

import java.util.List;
import java.util.Map;

@Component
public class CompareCompanyStatusTransformer implements ResultTransformable<Results> {

    private final CompareFieldsResultsTransformer transformer;
    private final CompareCompanyStatusResultMapper mapper;

    @Autowired
    public CompareCompanyStatusTransformer(CompareFieldsResultsTransformer transformer, CompareCompanyStatusResultMapper mapper) {
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
