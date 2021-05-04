package uk.gov.companieshouse.reconciliation.company;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws2.ses.Ses2Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Trigger a email broadcast to relevant stakeholders located inside the mailing list property.
 * Inside the email would be the results gathered from the different comparison jobs ran against company profiles.
 *
 * The following request headers should be set when a message is sent to this route:
 *
 * Ses2Constants.SUBJECT: The subject or title which should be set for the email when its sent out.
 * Ses2Constants.TO: The recipients who would be receiving the email (configurable via applications.properties).
 */
@Component
public class CompanyEmailTrigger extends RouteBuilder {

    @Value("${aws.mailing.list}")
    private List<String> mailingList;

    @Override
    public void configure() throws Exception {
        from("{{function.name.company_email}}")
                .setHeader(Ses2Constants.SUBJECT, simple("Company Profile Comparisons (${date:now:dd/MM/yyyy})"))
                .setHeader(Ses2Constants.TO, constant(mailingList))
                .setHeader("Destination", simple("{{endpoint.ses.broadcast_email}}"))
                .toD("{{function.name.send_email}}");
    }
}
