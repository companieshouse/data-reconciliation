package uk.gov.companieshouse.reconciliation.model;

import java.time.LocalDate;

public class EmailSendData {

    private String to;
    private String subject;
    private LocalDate date;

    public EmailSendData(String to, String subject, LocalDate date) {
        this.to = to;
        this.subject = subject;
        this.date = date;
    }

    public String getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public LocalDate getDate() {
        return date;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String to;
        private String subject;
        private LocalDate date;

        public Builder withTo(String to) {
            this.to = to;
            return this;
        }

        public Builder withSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder withDate(LocalDate date) {
            this.date = date;
            return this;
        }

        public EmailSendData build() {
            return new EmailSendData(to, subject, date);
        }
    }
}
