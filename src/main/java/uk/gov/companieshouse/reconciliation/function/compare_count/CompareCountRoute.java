package uk.gov.companieshouse.reconciliation.function.compare_count;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;
import uk.gov.companieshouse.reconciliation.function.compare_count.transformer.CompareCountTransformer;

import java.math.BigDecimal;
import java.util.Collections;

/**
 * Compare counts of resources from two endpoints with each other.
 *
 * The following request headers should be set when a message is sent to this route:
 *
 * Src: The first endpoint from which a resource count will be obtained.
 * SrcName: A human-readable description of the first endpoint.
 * Target: The second endpoint from which a resource count will be obtained.
 * TargetName: A human-readable description of the second endpoint.
 * Comparison: A human-readable description of the thing being compared.
 * Destination: The endpoint to which results will be sent.
 *
 * The response body will contain a brief description about which endpoint has more resources than the other. The
 * following response headers will also be set by this route:
 *
 * Weight: A signed integer. If Weight < 0 then the second endpoint has more resources than the first endpoint. If
 * Weight = 0 then both endpoints have an equal number of endpoints. If Weight > 0 then the first endpoint has more
 * resources than the first endpoint.
 * WeightAbs: An unsigned integer indicating how many more resources one endpoint has than another.
 */
@Component
public class CompareCountRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("direct:compare_count")
                .enrich().simple("${header.Src}").aggregationStrategy((base, src) -> {
                    base.getIn().setHeader("SrcCount", src.getMessage().getBody(BigDecimal.class));
                    return base;
                })
                .enrich().simple("${header.Target}").aggregationStrategy((base, target) -> {
                    base.getIn().setHeader("TargetCount", target.getMessage().getBody(BigDecimal.class));
                    return base;
                })
                .process(exchange -> {
                    BigDecimal weight = exchange.getIn().getHeader("SrcCount", BigDecimal.class)
                            .subtract(exchange.getIn().getHeader("TargetCount", BigDecimal.class));
                    exchange.getIn().setHeader("Weight", weight);
                    exchange.getIn().setHeader("WeightAbs", weight.abs());
                })
                .enrich().simple(("${header.Src}")).aggregationStrategy((base, src) -> {
                    base.getIn().setHeader("SrcList", new ResourceList(Collections.singletonList(src.getIn().getHeader("SrcCount", String.class)),
                            src.getIn().getHeader("SrcName", String.class)));
                    return base;
                })
                .enrich().simple("${header.Target}").aggregationStrategy((base, target) -> {
                    base.getIn().setHeader("TargetList", new ResourceList(Collections.singletonList(target.getIn().getHeader("TargetCount", String.class)),
                            target.getIn().getHeader("TargetName", String.class)));
                    return base;
                })
                .bean(CompareCountTransformer.class)
                .marshal().csv()
                .toD("${header.Upload}")
                .toD("${header.Presign}")
                .choice()
                    .when(header("Weight").isLessThan(0))
                        .setHeader("CompareCountBody", simple("${header.TargetName} has ${header.WeightAbs} more ${header.Comparison} than ${header.SrcName}." +
                                "\nResults available at ${body}"))
                    .when(header("Weight").isGreaterThan(0))
                        .setHeader("CompareCountBody", simple("${header.SrcName} has ${header.WeightAbs} more ${header.Comparison} than ${header.TargetName}." +
                                "\nResults available at ${body}"))
                    .otherwise()
                        .setHeader("CompareCountBody",simple("${header.SrcName} and ${header.TargetName} contain the same number of ${header.Comparison}." +
                                "\nResults available at ${body}"))
                .end()
                .log("${header.CompareCountBody}")

                .to("{{function.name.company_email}}");
    }
}
