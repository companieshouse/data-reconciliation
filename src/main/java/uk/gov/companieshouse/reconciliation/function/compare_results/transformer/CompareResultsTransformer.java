package uk.gov.companieshouse.reconciliation.function.compare_results.transformer;

import org.apache.camel.Header;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.model.ResultModel;
import uk.gov.companieshouse.reconciliation.model.Results;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CompareResultsTransformer {

    public List<Map<String, Object>> transform(@Header("SrcList") Results srcResults,
                                               @Header("SrcDescription") String srcDescription,
                                               @Header("TargetList") Results targetResults,
                                               @Header("TargetDescription") String targetDescription,
                                               @Header("RecordType") String recordType) {

        // Remap Results objects to maps of company number and company name pairings. If value in one map is different add it to the results.
        List<Map<String, Object>> results = new ArrayList<>();

        addRow(results, recordType, recordType, srcDescription, srcDescription,
                targetDescription, targetDescription);

        Map<String, String> srcModels = generateMappings(srcResults.getResultModels());
        Map<String, String> targetModels = generateMappings(targetResults.getResultModels());

        srcModels.entrySet().stream()
                .filter(entry ->
                        targetModels.containsKey(entry.getKey()) && !targetModels.get(entry.getKey()).equals(entry.getValue())
                )
                .forEach(entry ->
                        addRow(results, recordType, entry.getKey(), srcDescription, entry.getValue(),
                                targetDescription, targetModels.get(entry.getKey()))
                );

        return results;
    }

    private void addRow(List<Map<String, Object>> results, String... rowDetails) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put(rowDetails[0], rowDetails[1]);
        row.put(rowDetails[2], rowDetails[3]);
        row.put(rowDetails[4], rowDetails[5]);
        results.add(row);
    }

    private Map<String, String> generateMappings(Collection<ResultModel> resultModels) {
        return resultModels.stream().collect(Collectors.toMap(ResultModel::getCompanyNumber, ResultModel::getCompanyName));
    }

}
