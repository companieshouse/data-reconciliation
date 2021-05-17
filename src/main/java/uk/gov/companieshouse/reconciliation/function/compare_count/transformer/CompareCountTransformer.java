package uk.gov.companieshouse.reconciliation.function.compare_count.transformer;

import org.apache.camel.Header;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Transforms two lists into a list of key-value pairings representing the total count of entries
 * respective to the endpoints they are exclusive to.
 */
@Component
public class CompareCountTransformer {

    /**
     * Transforms two lists into a list of key-value pairings representing the total count of entries
     * respective to the endpoints they are exclusive to.
     *
     * @param srcResourceList    A {@link ResourceList list of results} obtained from the first endpoint.
     * @param targetResourceList A {@link ResourceList list of results} obtained from the second endpoint.
     * @return A {@link java.util.List list} of {@link java.util.Map key-value pairings} representing the total count
     * of entries respective to the endpoint sources it was sent from.
     */
    public List<Map<String, Object>> transform(@Header("SrcList") ResourceList srcResourceList, @Header("TargetList") ResourceList targetResourceList) {
        return getResults(srcResourceList, targetResourceList);
    }

    private List<Map<String, Object>> getResults(ResourceList srcResourceList, ResourceList targetResourceList){
        List<Map<String, Object>> results = new ArrayList<>();
        addRow(results, srcResourceList.getResultDesc(), targetResourceList.getResultDesc());
        addRow(results, srcResourceList.getResultList().iterator().next(), targetResourceList.getResultList().iterator().next());
        return results;
    }

    private void addRow(List<Map<String, Object>> table, Object srcValue, Object targetValue) {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("src", srcValue);
        values.put("target", targetValue);
        table.add(values);
    }
}
