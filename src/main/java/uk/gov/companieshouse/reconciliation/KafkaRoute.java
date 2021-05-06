package uk.gov.companieshouse.reconciliation;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class KafkaRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("cron:kafkacron?schedule=0/30 * * * * ?")
                .setBody(constant("hello"))
                .to("kafka://email-send?schemaRegistryURL=chs-kafka:8081&brokers=chs-kafka:9092&valueSerializer=org.apache.kafka.common.serialization.ByteArraySerializer&requestRequiredAcks=-1&retries=10&maxBlockMs=1000&requestTimeoutMs=1000");
    }
}
