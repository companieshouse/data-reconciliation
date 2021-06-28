package uk.gov.companieshouse.reconciliation.function.compare_results.transformer;

import org.apache.camel.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.function.compare_results.mapper.CompareInsolvencyCaseCountsResultMapper;
import uk.gov.companieshouse.reconciliation.model.InsolvencyResults;

import java.util.List;
import java.util.Map;

@Component
public class CompareInsolvencyCasesTransformer implements ResultTransformable<InsolvencyResults> {

    private final CompareFieldsResultsTransformer transformer;
    private final CompareInsolvencyCaseCountsResultMapper mapper;

    @Autowired
    public CompareInsolvencyCasesTransformer(CompareFieldsResultsTransformer transformer, CompareInsolvencyCaseCountsResultMapper mapper) {
        this.transformer = transformer;
        this.mapper = mapper;
    }

    public List<Map<String, Object>> transform(@Header("SrcList") InsolvencyResults srcResults,
                                               @Header("SrcDescription") String srcDescription,
                                               @Header("TargetList") InsolvencyResults targetResults,
                                               @Header("TargetDescription") String targetDescription,
                                               @Header("RecordKey") String recordKey) {
        return transformer.transform(srcResults, srcDescription, targetResults, targetDescription, recordKey, mapper);
    }
}
