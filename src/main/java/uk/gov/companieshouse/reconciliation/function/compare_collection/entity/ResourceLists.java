package uk.gov.companieshouse.reconciliation.function.compare_collection.entity;

import java.util.List;

public class ResourceLists {

    private final List<ResourceList> allResources;
    private final String description;

    public ResourceLists(List<ResourceList> allResources, String description) {
        this.allResources = allResources;
        this.description = description;
    }

    /**
     * Add a new {@link ResourceList resource list} to this instance.
     *
     * @param e The element that will be added.
     * @return True if the element was added successfully, otherwise false.
     */
    public boolean add(ResourceList e) {
        return allResources.add(e);
    }

    /**
     * @return The number of {@link ResourceList resource lists} held by this instance.
     */
    public int size() {
        return allResources.size();
    }

    public List<ResourceList> getAllResources() {
        return allResources;
    }

    public String getDescription() {
        return description;
    }
}
