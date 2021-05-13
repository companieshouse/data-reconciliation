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
 * Route is responsible for sending out the required data to chs-email-sender,
 * which eventually makes it way to an email template created in chs-notification-api.
 *
 * Accomplished via: using below fields which creates the email-send kafka model schema.
 *
 * emailRecipientList: the list of recipients the email would go to.
 * emailApplicationId: applicationId as required by the email-send model, used for chs-notification-api.
 * emailMessageId: messageId as required by the email-send model.
 * emailMessageType: messageType as required by the email-send mode, used for chs-notification-api.
 * emailSender: the sender of the email, as required by the email-send model.
 * emailDateFormat: a date format passed to be used in the Created At field for the email-send-model.
 *
 * For the data field inside the email-send model, we contain the generic To, Subject, Date, fields - but also:
 * ResourceLink: keeps track of download links and relevant description for comparison results.
 * ResourceLinksWrapper: wrapper class to be used with Apache Camel to represent a list of ResourceLink.
 *
 * The email-send body is populated from values passed in from the properties file.
 * Than is marshaled into a Avro format, before finally being sent to the chs-email-sender service.
 *
 * NOTE: due to versions incompatibility on kafka-producer, the Content-Type header has to be removed.
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

    @Value("${email.message.type}")
    private String emailMessageType;

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
                                    .withSubject(exchange.getIn().getHeader("EmailSubject", String.class))
                                    .withResourceLinks(exchange.getIn()
                                            .getHeader("ResourceLinks", ResourceLinksWrapper.class).getDownloadLinkList())
                                    .withDate(exchange.getIn().getHeader("CompletionDate", String.class))
                                    .build()
                    )
                )
                .marshal().json()
                .process(exchange ->
                    exchange.getIn().setBody(email_send.newBuilder()
                            .setData(exchange.getIn().getBody(String.class))
                            .setAppId(emailApplicationId)
                            .setMessageId(emailMessageId)
                            .setMessageType(emailMessageType)
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
