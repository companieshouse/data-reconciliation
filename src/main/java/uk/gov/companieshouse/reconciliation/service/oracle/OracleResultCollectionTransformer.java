package uk.gov.companieshouse.reconciliation.service.oracle;

import org.apache.camel.Body;
import org.apache.camel.Header;
import org.apache.camel.Headers;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OracleResultCollectionTransformer {

    public void transform(@Body List<Map<String, Object>> resultSet, @Header("OracleDescription") String description,
                          @Header("OracleTargetHeader") String targetHeader, @Headers Map<String, Object> headers) {
        Set<String> results = resultSet.stream()
                .map(e -> e.get("RESULT"))
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.toSet());
        ResourceList result = new ResourceList(results, description);
        headers.put(targetHeader, result);
    }
}
