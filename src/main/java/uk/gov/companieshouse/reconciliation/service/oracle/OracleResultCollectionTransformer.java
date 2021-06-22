package uk.gov.companieshouse.reconciliation.service.oracle;

import org.apache.camel.Body;
import org.apache.camel.Header;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OracleResultCollectionTransformer {

    public ResourceList transform(@Body List<Map<String, Object>> resultSet, @Header("Description") String description) {
        Set<String> results = resultSet.stream()
                .map(e -> e.get("RESULT"))
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.toSet());
        return new ResourceList(results, description);
    }
}
