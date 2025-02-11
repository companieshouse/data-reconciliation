package uk.gov.companieshouse.reconciliation.service;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.kafka.common.KafkaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import uk.gov.companieshouse.reconciliation.config.aws.S3ClientConfig;
import uk.gov.companieshouse.reconciliation.model.ResourceLink;
import uk.gov.companieshouse.reconciliation.model.ResourceLinksWrapper;

import java.util.Collections;
import java.util.Comparator;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertNull;

@CamelSpringBootTest
@SpringBootTest
@DirtiesContext
@TestPropertySource(locations = "classpath:application-stubbed.properties")
@Import(S3ClientConfig.class)
public class KafkaRouteTest {

    @Autowired
    private CamelContext context;

    @Produce("direct:send-to-kafka")
    private ProducerTemplate kafkaRouteProducer;

    @EndpointInject("mock:kafka-endpoint")
    private MockEndpoint kafkaEndpoint;

    @EndpointInject("mock:shutdown")
    private MockEndpoint shutdownEndpoint;

    @BeforeEach
    void setUp() {
        kafkaEndpoint.reset();
        shutdownEndpoint.reset();
    }

    @Test
    void testSendMessageToKafka() throws InterruptedException {
        kafkaEndpoint.expectedMessageCount(1);
        shutdownEndpoint.expectedMessageCount(1);
        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setHeader("ResourceLinks", new ResourceLinksWrapper(Collections.unmodifiableSet(new TreeSet<ResourceLink>(Comparator.comparing(ResourceLink::getRank)){{add(new ResourceLink((short)1, "link", "description"));}})));
        exchange.getIn().setHeader("ComparisonGroup", "group");
        Exchange actual = kafkaRouteProducer.send(exchange);
        assertNull(actual.getIn().getHeader("Content-Type"));
        assertNull(actual.getIn().getHeader("ResourceLinks"));
        MockEndpoint.assertIsSatisfied(context);
    }

    @Test
    void testHandleKafkaException() throws InterruptedException {
        kafkaEndpoint.whenAnyExchangeReceived(exchange -> {
            throw new KafkaException("Failed");
        });
        kafkaEndpoint.expectedMessageCount(1);
        shutdownEndpoint.expectedMessageCount(1);
        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setHeader("ResourceLinks", new ResourceLinksWrapper(Collections.unmodifiableSet(new TreeSet<ResourceLink>(Comparator.comparing(ResourceLink::getRank)){{add(new ResourceLink((short)1, "link", "description"));}})));
        exchange.getIn().setHeader("ComparisonGroup", "group");
        Exchange actual = kafkaRouteProducer.send(exchange);
        assertNull(actual.getIn().getHeader("Content-Type"));
        assertNull(actual.getIn().getHeader("ResourceLinks"));
        MockEndpoint.assertIsSatisfied(context);
    }
}
