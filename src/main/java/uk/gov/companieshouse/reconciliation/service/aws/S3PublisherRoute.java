package uk.gov.companieshouse.reconciliation.service.aws;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class S3PublisherRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:s3-publisher")
                .toD("${header.Upload}")
                .toD("${header.Presign}")
                .setHeader("ResourceLinkReference", body());
    }
}
