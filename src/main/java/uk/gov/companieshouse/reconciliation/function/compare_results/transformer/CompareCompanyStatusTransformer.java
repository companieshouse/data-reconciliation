package uk.gov.companieshouse.reconciliation.function.compare_results.transformer;

import java.util.List;
import java.util.Map;
import org.apache.camel.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.function.compare_results.mapper.CompanyResultsMappable;
import uk.gov.companieshouse.reconciliation.function.compare_results.mapper.CompareCompanyStatusResultMapper;
import uk.gov.companieshouse.reconciliation.model.Results;

@Component
public class CompareCompanyStatusTransformer {

    private final CompareFieldsResultsTransformer transformer;
    private final CompanyResultsMappable mapper;

    @Autowired
    public CompareCompanyStatusTransformer(CompareFieldsResultsTransformer transformer) {
        this.transformer = transformer;
        this.mapper = new CompareCompanyStatusResultMapper();
    }

    public List<Map<String, Object>> transform(@Header("SrcList") Results srcResults,
            @Header("SrcDescription") String srcDescription,
            @Header("TargetList") Results targetResults,
            @Header("TargetDescription") String targetDescription,
            @Header("RecordKey") String recordKey) {
        return transformer.transform(srcResults, srcDescription, targetResults, targetDescription, recordKey, mapper::generateMappings);
    }
}
