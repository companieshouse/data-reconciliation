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
 * Upload: The endpoint which will be used to store CSV files into the S3 bucket.
 * Presign: The endpoint which will be used to download CSV files from specific S3 bucket.
 *
 * The response body will contain a brief description about which endpoint has more resources than the other.
 * The results would also than be marshalled into a CSV format to be stored inside a S3 bucket.
 * Than using the S3 bucket it would also generate a download link to the respective CSV file.
 *
 * The following response headers will also be set by this route:
 *
 * Weight: A signed integer. If Weight < 0 then the second endpoint has more resources than the first endpoint. If
 * Weight = 0 then both endpoints have an equal number of endpoints. If Weight > 0 then the first endpoint has more
 * resources than the first endpoint.
 * WeightAbs: An unsigned integer indicating how many more resources one endpoint has than another.
 * CompareCountBody: The body composed of results gathered from this comparison.
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
                .setHeader("CompareCountLink", body())
                .choice()
                    .when(header("Weight").isLessThan(0))
                        .setHeader("CompareCountDescription", simple("${header.TargetName} has ${header.WeightAbs} more ${header.Comparison} than ${header.SrcName}."))
                    .when(header("Weight").isGreaterThan(0))
                        .setHeader("CompareCountDescription", simple("${header.SrcName} has ${header.WeightAbs} more ${header.Comparison} than ${header.TargetName}."))
                    .otherwise()
                        .setHeader("CompareCountDescription",simple("${header.SrcName} and ${header.TargetName} contain the same number of ${header.Comparison}."))
                .log("Compare Count: ${header.CompareCountDescription}")
                .end()
                .to("{{endpoint.output}}");
    }
}
