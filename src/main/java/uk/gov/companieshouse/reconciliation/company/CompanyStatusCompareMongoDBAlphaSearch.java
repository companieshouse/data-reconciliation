package uk.gov.companieshouse.reconciliation.company;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import org.springframework.stereotype.Component;

@Component
public class CompanyStatusCompareMongoDBAlphaSearch extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("{{endpoint.company_status_mongo_alpha.timer}}")
                .autoStartup("{{company_status_mongo_alpha_enabled}}")
                .setHeader("Src").constant("{{endpoint.mongodb.wrapper.company_profile.collection}}")
                .setHeader("SrcDescription").constant("MongoDB - Company Profile")
                .setHeader("Target").constant("{{endpoint.elasticsearch.collection}}")
                .setHeader("TargetDescription").constant("Alpha Index")
                .setHeader("ElasticsearchEndpoint").constant("{{endpoint.elasticsearch.alpha}}")
                .setHeader("ElasticsearchQuery").constant("{{query.elasticsearch.alpha.company}}")
                .setHeader("ElasticsearchCacheKey").constant("{{endpoint.elasticsearch.alpha.cache.key}}")
                .setHeader("ElasticsearchTransformer").constant("{{transformer.elasticsearch.alpha}}")
                .setHeader("RecordKey").constant("Company Number")
                .setHeader("Comparison").constant("company statuses")
                .setHeader("ComparisonGroup", constant("Elasticsearch"))
                .setHeader("Destination").constant("{{endpoint.output}}")
                .setHeader("ResultsTransformer").constant("{{function.mapper.company_status}}")
                .setHeader("Upload", constant("{{endpoint.s3.upload}}"))
                .setHeader("Presign", constant("{{endpoint.s3presigner.download}}"))
                .setHeader("LinkId", constant("company-status-alpha-link"))
                .setHeader(AWS2S3Constants.KEY, simple("company/results_status_alpha_mongo_${date:now:yyyyMMdd}T${date:now:hhmmss}.csv"))
                .setHeader(AWS2S3Constants.DOWNLOAD_LINK_EXPIRATION_TIME, constant("{{aws.expiry}}"))
                .to("{{function.name.compare_results}}");
    }
}
