package uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll;

import org.apache.camel.spi.annotations.Component;
import org.apache.camel.support.DefaultComponent;
import uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll.client.ElasticsearchScrollingSearchClientFactory;

import java.util.Map;

@Component("es-bulk-load")
public class ElasticsearchSlicedScrollComponent extends DefaultComponent {
    protected ElasticsearchSlicedScrollEndpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        ElasticsearchSlicedScrollEndpoint endpoint = new ElasticsearchSlicedScrollEndpoint(uri, this, new ElasticsearchScrollingSearchClientFactory());
        setProperties(endpoint, parameters);
        return endpoint;
    }

    public void close() {
        //do nothing
    }
}
