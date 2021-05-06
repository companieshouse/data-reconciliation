package uk.gov.companieshouse.reconciliation;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.model.EmailSendData;
import uk.gov.companieshouse.reconciliation.model.EmailSendModel;

import java.time.LocalDate;

@Component
public class KafkaRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("cron:kafkacron?schedule=0/30 * * * * ?")
                .process(exchange -> {
                    exchange.getIn().setBody(
                            new EmailSendModel.Builder()
                                    .withApplicationId("data-reconciliation")
                                    .withMessageId("company-profile-email")
                                    .withMessageType("company-profile-email")
                                    .withEmailSendData(new EmailSendData.Builder()
                                            .withTo("kpang@companieshouse.gov.uk")
                                            .withSubject("Company profile comparisons")
                                            .withDate(LocalDate.now())
                                            .build())
                                   .withEmailAddress("test@companieshouse.gov.uk")
                                   .build()
                    );
                })
                .marshal().avro()
                .to("kafka://email-send?schemaRegistryURL=chs-kafka:8081&brokers=chs-kafka:9092&valueSerializer=org.apache.kafka.common.serialization.ByteArraySerializer&requestRequiredAcks=-1&retries=10&maxBlockMs=1000&requestTimeoutMs=1000");
    }
}
