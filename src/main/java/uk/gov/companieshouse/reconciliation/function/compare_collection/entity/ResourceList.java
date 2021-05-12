package uk.gov.companieshouse.reconciliation.function.compare_collection.entity;

import java.util.Collection;
import java.util.Collections;

/**
 * A list of resources fetched from an endpoint.
 */
public class ResourceList {

    private final Collection<String> resultList;
    private final String resultDesc;

    public ResourceList(Collection<String> resultList, String resultDesc) {
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
    public Collection<String> getResultList() {
        return Collections.unmodifiableCollection(resultList);
    }

    /**
     * Add a new item to this ResourceList instance.
     *
     * @param element The element that will be added.
     * @return True if the element was added successfully, otherwise false.
     */
    public boolean add(String element) {
        return resultList.add(element);
    }

    /**
     * @return The number of elements held by this ResourceList instance.
     */
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

