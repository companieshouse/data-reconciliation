package uk.gov.companieshouse.reconciliation.function.compare_results.transformer;

import java.util.function.Function;
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

/**
 * Compare two {@link Results results objects} and record any differences for each result with a
 * matching ID.
 */
@Component
public class CompareFieldsResultsTransformer {

    /**
     * Compare two {@link Results results objects} and record any differences for each result with a
     * matching ID.
     *
     * @param srcResults        A {@link Results results object} containing data retrieved from one
     *                          endpoint.
     * @param srcDescription    A description of the first endpoint from which {@link Results
     *                          results} have been retrieved.
     * @param targetResults     A {@link Results results object} containing data retrieved from
     *                          another endpoint.
     * @param targetDescription A description of the second endpoint from which {@link Results
     *                          results} have been retrieved.
     * @param recordType        A description of the type of data being compared.
     * @return A {@link List list} containing {@link Map company number and data name-value
     * pairings}.
     */
    public List<Map<String, Object>> transform(Results srcResults,
            String srcDescription,
            Results targetResults,
            String targetDescription,
            String recordType,
            Function<Collection<ResultModel>, Map<String, String>> mappingFunction) {

        // Calculate intersection of both Results objects.
        // Remap Results objects to maps of company number and company name pairings.
        // If value in one map is different add it to the results.
        List<Map<String, Object>> results = new ArrayList<>();

        addRow(results, recordType, recordType, srcDescription, srcDescription,
                targetDescription, targetDescription);

        Map<String, String> srcModels = mappingFunction.apply(srcResults.getResultModels());
        Map<String, String> targetModels = mappingFunction.apply(targetResults.getResultModels());

        srcModels.entrySet().stream()
                .filter(entry ->
                        targetModels.containsKey(entry.getKey()) && !targetModels
                                .get(entry.getKey()).equals(entry.getValue())
                )
                .forEach(entry ->
                        addRow(results, recordType, entry.getKey(), srcDescription,
                                entry.getValue(),
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


}
