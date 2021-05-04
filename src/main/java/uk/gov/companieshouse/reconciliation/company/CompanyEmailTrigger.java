package uk.gov.companieshouse.reconciliation.company;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws2.ses.Ses2Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

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
