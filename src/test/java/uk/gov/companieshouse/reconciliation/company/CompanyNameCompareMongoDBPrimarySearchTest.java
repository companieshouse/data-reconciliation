package uk.gov.companieshouse.reconciliation.company;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Projections;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.Collections;

@CamelSpringBootTest
@SpringBootTest
@DirtiesContext
@TestPropertySource(locations = "classpath:application-stubbed.properties")
public class CompanyNameCompareMongoDBPrimarySearchTest {

    @Autowired
    private CamelContext context;

    @EndpointInject("mock:compare_results")
    private MockEndpoint target;

    @Produce("direct:company_name_mongo_primary_trigger")
    private ProducerTemplate producerTemplate;

    @BeforeEach
    void setUp() {
        target.reset();
    }

    @Test
    void testSetHeadersAndProduceMessage() throws InterruptedException {
        target.expectedHeaderReceived("Src", "mock:mongoCompanyProfileCollection");
        target.expectedHeaderReceived("SrcDescription", "MongoDB - Company Profile");
        target.expectedHeaderReceived("MongoCacheKey", "mongoCompanyProfile");
        target.expectedHeaderReceived("MongoQuery", Collections.singletonList(Aggregates.project(Projections.include("_id", "data.company_name", "data.company_status"))));
        target.expectedHeaderReceived("MongoEndpoint", "mock:fruitBasket");
        target.expectedHeaderReceived("Target", "mock:elasticsearch-collection");
        target.expectedHeaderReceived("TargetDescription", "Primary Search Index");
        target.expectedHeaderReceived("ElasticsearchCacheKey", "elasticsearchPrimary");
        target.expectedHeaderReceived("ElasticsearchEndpoint", "mock:elasticsearch-stub");
        target.expectedHeaderReceived("ElasticsearchQuery", "test");
        target.expectedHeaderReceived("RecordKey", "Company Number");
        target.expectedHeaderReceived("Comparison", "company names");
        target.expectedHeaderReceived("Destination", "mock:result");
        target.expectedHeaderReceived("Upload", "mock:s3_bucket_destination");
        target.expectedHeaderReceived("Presign", "mock:s3_download_link");
        target.expectedHeaderReceived(AWS2S3Constants.DOWNLOAD_LINK_EXPIRATION_TIME, 2000L);
        producerTemplate.sendBody(0);
        MockEndpoint.assertIsSatisfied(context);
    }
}
