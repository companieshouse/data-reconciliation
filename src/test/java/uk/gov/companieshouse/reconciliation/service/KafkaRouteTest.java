package uk.gov.companieshouse.reconciliation.service;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import uk.gov.companieshouse.reconciliation.model.ResourceLink;
import uk.gov.companieshouse.reconciliation.model.ResourceLinksWrapper;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNull;

@CamelSpringBootTest
@SpringBootTest
@DirtiesContext
@TestPropertySource(locations = "classpath:application-stubbed.properties")
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
        exchange.getIn().setHeader("ResourceLinks", new ResourceLinksWrapper(Collections.singletonList(new ResourceLink("link", "description"))));
        Exchange actual = kafkaRouteProducer.send(exchange);
        assertNull(actual.getIn().getHeader("Content-Type"));
        assertNull(actual.getIn().getHeader("ResourceLinks"));
        MockEndpoint.assertIsSatisfied(context);
    }
}
