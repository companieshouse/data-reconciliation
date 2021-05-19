package uk.gov.companieshouse.reconciliation.function.compare_collection.transformer;

import org.apache.camel.Header;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Transforms two lists into a list of key-value pairings representing the symmetric difference between both lists
 * and the endpoints their entries are exclusive to.
 */
@Component
public class CompareCollectionTransformer {

    /**
     * Transform two lists into a list of key-value pairings representing the symmetric difference between both lists
     * and the endpoints their elements are exclusive to.
     * The headers of the two columns is set to Company Number & Exclusive To respectively.
     *
     * @param srcResourceList    A {@link ResourceList list of results} obtained from the first endpoint.
     * @param targetResourceList A {@link ResourceList list of results} obtained from the second endpoint.
     * @return A {@link java.util.List list} of {@link java.util.Map key-value pairings} representing the
     * symmetric difference between both lists and the endpoints their elements are exclusive to.
     */
    public List<Map<String, Object>> transform(@Header("SrcList") ResourceList srcResourceList,
                                               @Header("TargetList") ResourceList targetResourceList,
                                               @Header("RecordType") String recordType) {
        Set<String> allItems = union(srcResourceList, targetResourceList);
        return symmetricDifference(srcResourceList, targetResourceList, allItems, recordType);
    }

    private Set<String> union(ResourceList srcList, ResourceList targetList) {
        Set<String> allItems = new LinkedHashSet<>();
        allItems.addAll(srcList.getResultList());
        allItems.addAll(targetList.getResultList());
        return allItems;
    }

    private List<Map<String, Object>> symmetricDifference(ResourceList srcList, ResourceList targetList, Set<String> allItems, String recordType) {
        List<Map<String, Object>> result = new LinkedList<>();
        addResultHeaders(result, recordType);
        addRows(srcList, targetList, allItems, result, recordType);
        return result;
    }

    private void addResultHeaders(List<Map<String, Object>> result, String recordType) {
        Map<String, Object> headers = new LinkedHashMap<>();
        headers.put(recordType, recordType);
        headers.put("Exclusive To", "Exclusive To");
        result.add(headers);
    }

    private void addRows(ResourceList srcList, ResourceList targetList, Set<String> allItems, List<Map<String, Object>> result, String recordType) {
        for (String item : allItems) {
            if (srcList.contains(item) && !targetList.contains(item)) {
                addRow(item, srcList.getResultDesc(), result, recordType);
            } else if (!srcList.contains(item) && targetList.contains(item)) {
                addRow(item, targetList.getResultDesc(), result, recordType);
            }
        }
    }

    private void addRow(String item, String source, List<Map<String, Object>> results, String recordType) {
        Map<String, Object> row = new HashMap<>();
        row.put(recordType, item);
        row.put("Exclusive To", source);
        results.add(row);
    }
}
