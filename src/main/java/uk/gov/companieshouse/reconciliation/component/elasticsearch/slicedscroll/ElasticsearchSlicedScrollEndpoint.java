package uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;
import org.apache.camel.support.DefaultEndpoint;
import uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll.client.ElasticsearchScrollingSearchClientFactory;

import java.io.IOException;

@UriEndpoint(
        firstVersion = "0.0.1",
        scheme = "es-builk-load",
        title = "Bulk Loader for Elasticsearch",
        syntax = "es-bulk-load:loaderName?hostname=host&indexName=index",
        lenientProperties = true,
        producerOnly = true)
public class ElasticsearchSlicedScrollEndpoint extends DefaultEndpoint {

    @UriPath
    @Metadata(required = true)
    private String name;

    @UriParam
    @Metadata(required = true)
    private String indexName;

    @UriParam
    @Metadata(required = true)
    private String hostname;

    @UriParam(defaultValue = "9200")
    private Integer portNumber = 9200;

    @UriParam(defaultValue = "https")
    private String protocol = "https";

    @UriParam(defaultValue = "1")
    private Integer numberOfSegments = 1;

    @UriParam(defaultValue = "500")
    private Integer maximumSliceSize = 500;

    @UriParam(defaultValue = "_uid")
    private String sliceField = "_uid";

    @UriParam(defaultValue = "60")
    private Long timeoutInSeconds = 60L;

    private final ElasticsearchScrollingSearchClientFactory clientFactory;

    public ElasticsearchSlicedScrollEndpoint(String endpointUri, Component component, ElasticsearchScrollingSearchClientFactory clientFactory) {
        super(endpointUri, component);
        this.clientFactory = clientFactory;
    }

    public Producer createProducer() throws Exception {
        return new ElasticsearchSlicedScrollProducer(this, clientFactory.build(hostname, portNumber, protocol, indexName, maximumSliceSize, timeoutInSeconds, sliceField));
    }

    public Consumer createConsumer(Processor processor) {
        throw new UnsupportedOperationException("Consumer not supported");
    }

    public void close() throws IOException {
        //do nothing
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Integer getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(Integer portNumber) {
        this.portNumber = portNumber;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Integer getNumberOfSegments() {
        return numberOfSegments;
    }

    public void setNumberOfSegments(Integer numberOfSegments) {
        this.numberOfSegments = numberOfSegments;
    }

    public Integer getMaximumSliceSize() {
        return maximumSliceSize;
    }

    public void setMaximumSliceSize(Integer maximumSliceSize) {
        this.maximumSliceSize = maximumSliceSize;
    }

    public Long getTimeoutInSeconds() {
        return timeoutInSeconds;
    }

    public void setTimeoutInSeconds(Long timeoutInSeconds) {
        this.timeoutInSeconds = timeoutInSeconds;
    }

    public String getSliceField() {
        return sliceField;
    }

    public void setSliceField(String sliceField) {
        this.sliceField = sliceField;
    }

    public ElasticsearchScrollingSearchClientFactory getClientFactory() {
        return clientFactory;
    }
}
