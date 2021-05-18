package uk.gov.companieshouse.reconciliation.model;

import java.util.Collection;
import java.util.Collections;

public class Results {

    private final Collection<ResultModel> resultModels;

    public Results(Collection<ResultModel> resultModels) {
        this.resultModels = resultModels;
    }

    public void addResult(ResultModel resultModel) {
        resultModels.add(resultModel);
    }

    public Collection<ResultModel> getResultModels() {
        return Collections.unmodifiableCollection(resultModels);
    }
}
