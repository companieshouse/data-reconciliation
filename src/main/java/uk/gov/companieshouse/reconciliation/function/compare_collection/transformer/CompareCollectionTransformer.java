package uk.gov.companieshouse.reconciliation.function.compare_collection.transformer;

import org.apache.camel.Header;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Transforms two lists into a list of key-value pairings representing the symmetric difference between both lists
 * and the endpoints their entries are exclusive to.
 */
@Component
public class CompareCollectionTransformer {

    /**
     * Transform two lists into a list of key-value pairings representing the symmetric difference between both lists
     * and the endpoints their elements are exclusive to.
     *
     * @param srcResourceList    A {@link ResourceList list of results} obtained from the first endpoint.
     * @param targetResourceList A {@link ResourceList list of results} obtained from the second endpoint.
     * @return A {@link java.util.List list} of {@link java.util.Map key-value pairings} representing the
     * symmetric difference between both lists and the endpoints their elements are exclusive to.
     */
    public List<Map<String, Object>> transform(@Header("SrcList") ResourceList srcResourceList, @Header("TargetList") ResourceList targetResourceList) {
        List<String> allItems = union(srcResourceList, targetResourceList);
        return symmetricDifference(srcResourceList, targetResourceList, allItems);
    }

    private List<String> union(ResourceList srcList, ResourceList targetList) {
        List<String> allItems = new ArrayList<>();
        allItems.addAll(srcList.getResultList());
        allItems.addAll(targetList.getResultList());
        return allItems;
    }

    private List<Map<String, Object>> symmetricDifference(ResourceList srcList, ResourceList targetList, List<String> allItems) {
        List<Map<String, Object>> result = new ArrayList<>();
        addResultHeaders(result);
        result.addAll(allItems
                .stream()
                .filter(a -> !(srcList.contains(a) && targetList.contains(a)))
                .collect(ArrayList::new, (a, b) -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("Company Number", b);
                    if (srcList.contains(b)) {
                        row.put("Exclusive To", srcList.getResultDesc());
                    } else {
                        row.put("Exclusive To", targetList.getResultDesc());
                    }
                    a.add(row);
                }, List::addAll));
        return result;
    }

    private void addResultHeaders(List<Map<String, Object>> result) {
        Map<String, Object> headers = new LinkedHashMap<>();
        headers.put("Company Number", "Company Number");
        headers.put("Exclusive To", "Exclusive To");
        result.add(headers);
    }
}
