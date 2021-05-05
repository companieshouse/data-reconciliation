package uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll.client;

public class ElasticsearchSlicedScrollValidator {

    public boolean validateSliceConfiguration(int sliceId, int noOfSlices) {
        return sliceId >= 0 && noOfSlices > 1 && sliceId < noOfSlices;
    }
}
