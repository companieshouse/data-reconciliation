package uk.gov.companieshouse.reconciliation.service.oracle;

import org.apache.camel.Body;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class OracleResultSplitter {
    public Iterator<String> split(@Body List<Map<String, Object>> result) {
        return result.stream()
                .map(e -> (String)e.get("RESULT"))
                .filter(Objects::nonNull)
                .collect(Collectors.toList()).iterator();
    }
}
