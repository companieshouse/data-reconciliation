package uk.gov.companieshouse.reconciliation.email;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import uk.gov.companieshouse.reconciliation.model.ResourceLinksWrapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

@CamelSpringBootTest
@SpringBootTest
@DirtiesContext
@TestPropertySource(locations = "classpath:application-stubbed.properties")
@UseAdviceWith
public class SendElasticsearchEmailRouteTest {

    @Autowired
    private ModelCamelContext context;

    @EndpointInject("mock:kafka-endpoint")
    private MockEndpoint kafkaEndpoint;

    @Produce("direct:send-elasticsearch-email")
    private ProducerTemplate producerTemplate;

    @AfterEach
    void after() {
        kafkaEndpoint.reset();
    }

    @Test
    void testSendEmailAggregatesThreeMessages() throws Exception {
        AdviceWith.adviceWith(context.getRouteDefinitions().get(0), context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                interceptSendToEndpoint("mock:kafka-endpoint")
                        .process(exchange -> {
                            ResourceLinksWrapper downloadsList = exchange.getIn().getHeader("ResourceLinks", ResourceLinksWrapper.class);
                            assertEquals(3, downloadsList.getDownloadLinkList().size());
                        });
            }
        });
        context.start();

        Exchange firstExchange = ExchangeBuilder.anExchange(context)
                .withHeader("ResourceLinkReference", "Compare Name Primary Mongo Link")
                .build();

        Exchange secondExchange = ExchangeBuilder.anExchange(context)
                .withHeader("ResourceLinkReference", "Compare Number Alpha Mongo Link")
                .build();

        Exchange thirdExchange = ExchangeBuilder.anExchange(context)
                .withHeader("ResourceLinkReference", "Compare Number Primary Mongo Link")
                .build();

        kafkaEndpoint.expectedMessageCount(1);
        producerTemplate.send(firstExchange);
        producerTemplate.send(secondExchange);
        producerTemplate.send(thirdExchange);

        MockEndpoint.assertIsSatisfied(context);
    }

}
