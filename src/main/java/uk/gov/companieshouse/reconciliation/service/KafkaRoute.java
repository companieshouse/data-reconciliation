package uk.gov.companieshouse.reconciliation.service;

import email.email_send;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.model.EmailSendData;
import uk.gov.companieshouse.reconciliation.model.ResourceLinksWrapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 *
 */
@Component
public class KafkaRoute extends RouteBuilder {

    private static final String EMAIL_SUBJECT = "Company profile comparisons";

    @Value("${email.recipient.list}")
    private String emailRecipientList;

    @Value("${email.application.id}")
    private String emailApplicationId;

    @Value("${email.message.id}")
    private String emailMessageId;

    @Value("${email.sender}")
    private String emailSender;

    @Value("${email.date.format}")
    private String emailDateFormat;

    @Override
    public void configure() throws Exception {
        from("direct:send-to-kafka")
                .process(exchange ->
                    exchange.getIn().setBody(
                            EmailSendData.builder()
                                    .withTo(emailRecipientList)
                                    .withSubject(EMAIL_SUBJECT)
                                    .withResourceLinks(exchange.getIn()
                                            .getHeader("ResourceLinks", ResourceLinksWrapper.class).getDownloadLinkList())
                                    .withDate(LocalDate.now().format(DateTimeFormatter.ofPattern(emailDateFormat)))
                                    .build()
                    )
                )
                .marshal().json()
                .process(exchange ->
                    exchange.getIn().setBody(email_send.newBuilder()
                            .setData(exchange.getIn().getBody(String.class))
                            .setAppId(emailApplicationId)
                            .setMessageId(emailMessageId)
                            .setMessageType(emailApplicationId)
                            .setEmailAddress(emailSender)
                            .setCreatedAt(LocalDate.now().format(DateTimeFormatter.ofPattern(emailDateFormat)))
                            .build())
                )
                .marshal().avro()
                .process(exchange ->
                    exchange.getIn().removeHeader("Content-Type")
                )
                .to("{{endpoint.kafka}}");
    }
}
