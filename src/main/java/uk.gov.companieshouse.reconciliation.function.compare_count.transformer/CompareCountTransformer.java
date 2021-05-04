package uk.gov.companieshouse.reconciliation.function.compare_count.transformer;

import org.apache.camel.Header;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class CompareCountTransformer {
    public List<Map<String, Object>> transform(@Header("SrcList") ResourceList srcResourceList, @Header("TargetList") ResourceList targetResourceList) {
        return getResults(srcResourceList, targetResourceList);
    }

    private List<Map<String, Object>> getResults(ResourceList srcResourceList, ResourceList targetResourceList){
        List<Map<String, Object>> results = new ArrayList<>();

        Map<String, Object> names = new LinkedHashMap<>();
        names.put("srcName", srcResourceList.getResultDesc());
        names.put("targetName", targetResourceList.getResultDesc());
        results.add(names);

        Map<String, Object> counts = new LinkedHashMap<>();
        counts.put("srcCount", srcResourceList.getResultList().get(0));
        counts.put("targetCount", targetResourceList.getResultList().get(0));
        results.add(counts);

        return results;
    }

}
