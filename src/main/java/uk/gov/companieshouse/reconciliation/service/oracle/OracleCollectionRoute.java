package uk.gov.companieshouse.reconciliation.service.oracle;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;

import java.util.Collections;
import java.util.LinkedList;

@Component
public class OracleCollectionRoute extends RouteBuilder {

    @Value("${endpoint.oracle.threads}")
    private int numberOfThreads;

    @Override
    public void configure() throws Exception {
        from("direct:oracle-collection")
                .setBody(header("OracleQuery"))
                .enrich()
                .simple("${header.OracleEndpoint}")
                .split()
                .method(OracleResultSplitter.class)
                .aggregationStrategy((prev, curr) -> {
                    String result = curr.getIn().getBody(String.class);
                    if(prev == null) {
                        ResourceList resourceList = new ResourceList(Collections.synchronizedList(new LinkedList<>()), curr.getIn().getHeader("OracleDescription", String.class));
                        if(result != null) {
                            resourceList.add(result);
                        }
                        curr.getIn().setHeader(curr.getIn().getHeader("OracleTargetHeader", String.class), resourceList);
                        return curr;
                    }
                    ResourceList resourceList = prev.getIn().getHeader(prev.getIn().getHeader("OracleTargetHeader", String.class), ResourceList.class);
                    if(result != null) {
                        resourceList.add(result);
                    }
                    return prev;
                })
                .process();
    }
}
