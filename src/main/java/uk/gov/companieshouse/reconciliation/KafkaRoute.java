package uk.gov.companieshouse.reconciliation;

import email.email_send;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.model.EmailSendData;

import java.time.LocalDate;

@Component
public class KafkaRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("cron:kafkacron?schedule=0/30 * * * * ?")
                .process(exchange -> {
                    exchange.getIn().setBody(
                            EmailSendData.builder()
                                    .withTo("kpang@companieshouse.gov.uk")
                                    .withSubject("Company profile comparisons")
                                    .withDate(LocalDate.now())
                                    .build()
                    );
                })
                .marshal().json()
                .process(exchange -> {
                    exchange.getIn().setBody(email_send.newBuilder()
                            .setData(exchange.getIn().getBody(String.class))
                            .setAppId("data-reconciliation")
                            .setMessageId("company-profile-email")
                            .setEmailAddress("dgroves@companieshouse.gov.uk")
                            .setMessageType("company-profile-email")
                            .setCreatedAt("01 January 1980")
                            .build());
                })
                .marshal().avro()
                .process(exchange -> {
                    exchange.getIn().removeHeader("Content-Type");
                })
                .to("kafka://email-send?schemaRegistryURL=chs-kafka:8081&brokers=chs-kafka:9092&valueSerializer=org.apache.kafka.common.serialization.ByteArraySerializer&requestRequiredAcks=-1&retries=10&maxBlockMs=1000&requestTimeoutMs=1000");
    }
}
