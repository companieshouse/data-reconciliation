package uk.gov.companieshouse.reconciliation.function.compare_results.transformer;

import org.apache.camel.Header;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.model.ResultModel;
import uk.gov.companieshouse.reconciliation.model.Results;

import java.util.*;

@Component
public class CompareResultsTransformer {

    public List<Map<String, Object>> transform(@Header("SrcList") Results srcResults,
                                               @Header("TargetList") Results targetResults,
                                               @Header("RecordType") String recordType) {

        // Remap Results objects to maps of company number and company name pairings. If value in one map is different add it to the results.
        List<Map<String, Object>> results = new ArrayList<>();
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("Company Number", "Company Number");
        row.put("MongoDB - Company Profile", "MongoDB - Company Profile");
        row.put("Primary Search Index", "Primary Search Index");
        results.add(row);
        Map<String, String> srcModels = new HashMap<>();
        Map<String, String> targetModels = new HashMap<>();

        for (ResultModel model : srcResults.getResultModels()) {
            srcModels.put(model.getCompanyNumber(), model.getCompanyName());
        }

        for (ResultModel model : targetResults.getResultModels()) {
            targetModels.put(model.getCompanyNumber(), model.getCompanyName());
        }

        for (Map.Entry<String, String> entry : srcModels.entrySet()) {
            row = new LinkedHashMap<>();
            if (targetModels.containsKey(entry.getKey()) && !targetModels.get(entry.getKey()).equals(entry.getValue())) {
                row.put("Company Number", entry.getKey());
                row.put("MongoDB - Company Profile", entry.getValue());
                row.put("Primary Search Index", targetModels.get(entry.getKey()));
                results.add(row);
            }
        }

        return results;
    }

}
