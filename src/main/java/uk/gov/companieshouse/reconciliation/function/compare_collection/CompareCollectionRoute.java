package uk.gov.companieshouse.reconciliation.function.compare_collection;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;
import uk.gov.companieshouse.reconciliation.function.compare_collection.transformer.CompareCollectionTransformer;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Compare lists of resources from two endpoints with each other.
 *
 * The following request headers should be set when a message is sent to this route:
 *
 * Src: The first endpoint from which a list of resources will be obtained.
 * SrcName: A human-readable description of the first endpoint.
 * Target: The second endpoint from which a list of resources will be obtained.
 * TargetName: A human-readable description of the second endpoint.
 * Destination: The endpoint to which results will be sent.
 *
 * The response body will tabulate which resources are exclusive to each endpoint. The following response headers are
 * set by this route:
 *
 * SrcList: A list of resources obtained from the first endpoint.
 * TargetList: A list of resources obtained from the second endpoint.
 */
@Component
public class CompareCollectionRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:compare_collection")
                .enrich()
                .simple("${header.Src}")
                .aggregationStrategy((base, src) -> {
                    List<?> targetBody = src.getIn().getBody(List.class);
                    List<String> targetClean = targetBody.stream()
                            .map(obj -> {
                                return Optional.ofNullable((Map<?, ?>) obj)
                                        .map(e -> e.get("RESULT"))
                                        .map(Object::toString)
                                        .orElse(null);
                            })
                            .collect(Collectors.toList());
                    base.getIn().setHeader("SrcList", new ResourceList(targetClean, base.getIn().getHeader("SrcName", String.class)));
                    return base;
                })
                .enrich()
                .simple("${header.Target}")
                .aggregationStrategy((base, target) -> {
                    List<?> targetBody = target.getIn().getBody(List.class);
                    List<String> targetClean = targetBody.stream()
                            .map(obj -> (String) obj)
                            .collect(Collectors.toList());
                    base.getIn().setHeader("TargetList", new ResourceList(targetClean, base.getIn().getHeader("TargetName", String.class)));
                    return base;
                })
                .bean(CompareCollectionTransformer.class)
                .marshal().csv()
                .toD("${header.Destination}");
    }
}
