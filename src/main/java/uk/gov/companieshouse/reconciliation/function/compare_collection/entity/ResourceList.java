package uk.gov.companieshouse.reconciliation.function.compare_collection.entity;

import java.util.Collections;
import java.util.List;

/**
 * A list of resources fetched from an endpoint.
 */
public class ResourceList {

    private final List<String> resultList;
    private final String resultDesc;

    public ResourceList(List<String> resultList, String resultDesc) {
        this.resultList = resultList;
        this.resultDesc = resultDesc;
    }

    /**
     * @param item The item against which an existence check will be performed.
     * @return True if the item exists in the list of resources, else false.
     */
    public boolean contains(String item) {
        return resultList.contains(item);
    }

    /**
     * @return A view of the list of resources encapsulated by this ResourceList instance.
     */
    public List<String> getResultList() {
        return Collections.unmodifiableList(resultList);
    }

    public boolean add(String e) {
        return resultList.add(e);
    }

    public int size() {
        return resultList.size();
    }

    /**
     * @return A description of the endpoint this list of resources was obtained from.
     */
    public String getResultDesc() {
        return resultDesc;
    }
}

