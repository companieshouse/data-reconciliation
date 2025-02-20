package uk.gov.companieshouse.reconciliation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {

    public static final String APPLICATION_NAMESPACE = "data-reconciliation";

    public static void main(String[] args) throws Exception {
        SpringApplication.run(App.class, args);
    }
}