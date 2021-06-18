package uk.gov.companieshouse.reconciliation.company;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import org.springframework.stereotype.Component;

/**
 * Trigger a comparison between company profiles in MongoDB and company profiles that have been indexed in the
 * Elasticsearch alphabetical search index. Any differences between company names will be recorded in the results.
 */
@Component
public class CompanyNameCompareMongoDBAlphaSearch extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("{{endpoint.company_name_mongo_alpha.timer}}")
                .setHeader("Src").constant("{{endpoint.mongodb.wrapper.company_profile.collection}}")
                .setHeader("SrcDescription").constant("MongoDB - Company Profile")
                .setHeader("Target").constant("{{endpoint.elasticsearch.collection}}")
                .setHeader("TargetDescription").constant("Alpha Index")
                .setHeader("ElasticsearchEndpoint").constant("{{endpoint.elasticsearch.alpha}}")
                .setHeader("ElasticsearchQuery").constant("{{query.elasticsearch.alpha.company}}")
                .setHeader("ElasticsearchCacheKey").constant("{{endpoint.elasticsearch.alpha.cache.key}}")
                .setHeader("ElasticsearchTransformer").constant("{{transformer.elasticsearch.alpha}}")
                .setHeader("RecordKey").constant("Company Number")
                .setHeader("Comparison").constant("company names")
                .setHeader("ComparisonGroup", constant("Elasticsearch"))
                .setHeader("Destination").constant("{{endpoint.output}}")
                .setHeader("ResultsTransformer").constant("{{function.mapper.company_name}}")
                .setHeader("Upload", constant("{{endpoint.s3.upload}}"))
                .setHeader("Presign", constant("{{endpoint.s3presigner.download}}"))
                .setHeader("EmailId", constant("elastic-search-email"))
                .setHeader("LinkId", constant("compare-company-name-alpha"))
                .setHeader(AWS2S3Constants.KEY, simple("company/results_name_alpha_mongo_${date:now:yyyyMMdd}T${date:now:hhmmss}.csv"))
                .setHeader(AWS2S3Constants.DOWNLOAD_LINK_EXPIRATION_TIME, constant("{{aws.expiry}}"))
                .to("{{function.name.compare_results}}");
    }
}
