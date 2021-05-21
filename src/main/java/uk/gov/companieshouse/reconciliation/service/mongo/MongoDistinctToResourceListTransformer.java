package uk.gov.companieshouse.reconciliation.service.mongo;

import org.apache.camel.Body;
import org.apache.camel.Header;
import org.apache.camel.Headers;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class MongoDistinctToResourceListTransformer {

    public void transform(@Body List<String> results, @Header("MongoDescription") String description,
                          @Header("MongoTargetHeader") String targetHeader, @Headers Map<String, Object> headers) {
        Set<String> disqualifications = results.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        headers.put(targetHeader, new ResourceList(disqualifications, description));
    }
}
