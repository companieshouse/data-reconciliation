package uk.gov.companieshouse.reconciliation.function.compare_count;

import com.mongodb.MongoException;
import org.apache.camel.LoggingLevel;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.common.RetryableRoute;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;
import uk.gov.companieshouse.reconciliation.function.compare_count.transformer.CompareCountTransformer;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Optional;

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
 */
@Component
public class CompareCountRoute extends RetryableRoute {

    @Override
    @SuppressWarnings("unchecked")
    public void configure() {
        super.configure();
        from("direct:compare_count")
                .onException(SQLException.class, MongoException.class)
                    .setHeader("ResourceLinkDescription", simple("Failed to perform ${header.ComparisonDescription}"))
                    .setHeader("Failed").constant(true)
                    .log(LoggingLevel.ERROR, "Compare count failed: ${header.ResourceLinkDescription}")
                    .handled(false)
                    .toD("${header.Destination}")
                .end()
                .enrich().simple("${header.Src}").aggregationStrategy((base, src) -> {
                    BigDecimal result = Optional.ofNullable(src.getMessage().getBody(BigDecimal.class)).orElse(BigDecimal.ZERO);
                    base.getIn().setHeader("SrcCount", result);
                    base.getIn().setHeader("SrcList", new ResourceList(Collections.singletonList(result.toString()),
                            src.getIn().getHeader("SrcName", String.class)));
                    return base;
                })
                .enrich().simple("${header.Target}").aggregationStrategy((base, target) -> {
                    BigDecimal result = Optional.ofNullable(target.getMessage().getBody(BigDecimal.class)).orElse(BigDecimal.ZERO);
                    base.getIn().setHeader("TargetCount", result);
                    base.getIn().setHeader("TargetList", new ResourceList(Collections.singletonList(result.toString()),
                            target.getIn().getHeader("TargetName", String.class)));
                    return base;
                })
                .process(exchange -> {
                    BigDecimal weight = exchange.getIn().getHeader("SrcCount", BigDecimal.class)
                            .subtract(exchange.getIn().getHeader("TargetCount", BigDecimal.class));
                    exchange.getIn().setHeader("Weight", weight);
                    exchange.getIn().setHeader("WeightAbs", weight.abs());
                })
                .bean(CompareCountTransformer.class)
                .marshal().csv()
                .choice()
                    .when(header("Weight").isLessThan(0))
                        .setHeader("ResourceLinkDescription", simple("${header.TargetName} has ${header.WeightAbs} more ${header.Comparison} than ${header.SrcName}"))
                    .when(header("Weight").isGreaterThan(0))
                        .setHeader("ResourceLinkDescription", simple("${header.SrcName} has ${header.WeightAbs} more ${header.Comparison} than ${header.TargetName}"))
                    .otherwise()
                        .setHeader("ResourceLinkDescription", simple("${header.SrcName} and ${header.TargetName} contain the same number of ${header.Comparison}"))
                .end()
                .log("Completed ${header.ComparisonDescription}.")
                .toD("${header.Destination}");
    }
}
