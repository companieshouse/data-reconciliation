package uk.gov.companieshouse.reconciliation.insolvency;

import com.mongodb.client.model.Filters;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.mongodb.MongoDbConstants;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

@CamelSpringBootTest
@SpringBootTest
@DirtiesContext
@TestPropertySource(locations = "classpath:application-stubbed.properties")
public class InsolvencyCompanyNumberCompareTest {

    @Autowired
    private CamelContext context;

    @EndpointInject("mock:compare_collection")
    private MockEndpoint compareCollection;

    @Produce("direct:insolvency_company_number_trigger")
    private ProducerTemplate producerTemplate;

    @Test
    void testSetHeadersAndProduceMessage() throws InterruptedException {
        compareCollection.expectedHeaderReceived("OracleQuery", "SELECT '12345678' FROM DUAL");
        compareCollection.expectedHeaderReceived("OracleEndpoint", "mock:fruitTree");
        compareCollection.expectedHeaderReceived("SrcDescription", "Oracle");
        compareCollection.expectedHeaderReceived("Src", "direct:oracle-collection");
        compareCollection.expectedHeaderReceived("TargetDescription", "MongoDB");
        compareCollection.expectedHeaderReceived("Target", "mock:mongoDistinctCollection");
        compareCollection.expectedHeaderReceived(MongoDbConstants.DISTINCT_QUERY_FIELD, "_id");
        compareCollection.expectedHeaderReceived("MongoDistinctEndpoint", "mock:insolvency_target");
        compareCollection.expectedHeaderReceived("MongoDistinctCacheKey", "mongoInsolvencies");
        compareCollection.expectedHeaderReceived("MongoQuery", Filters.exists("data.cases.number", true));
        compareCollection.expectedHeaderReceived("Comparison", "company insolvency cases");
        compareCollection.expectedHeaderReceived("ComparisonGroup", "Company insolvency");
        compareCollection.expectedHeaderReceived("RecordType", "Company Number");
        compareCollection.expectedHeaderReceived("Destination", "mock:result");
        compareCollection.expectedHeaderReceived("Upload", "mock:s3_bucket_destination");
        compareCollection.expectedHeaderReceived("Presign", "mock:s3_download_link");
        producerTemplate.sendBody(0);
        MockEndpoint.assertIsSatisfied(context);
    }
}
