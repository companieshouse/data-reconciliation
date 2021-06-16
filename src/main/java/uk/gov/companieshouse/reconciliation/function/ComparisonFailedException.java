package uk.gov.companieshouse.reconciliation.function;

/**
 * A comparison between two data sets has failed.
 */
public class ComparisonFailedException extends RuntimeException {

    public ComparisonFailedException(String message) {
        super(message);
    }

    public ComparisonFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ComparisonFailedException(Throwable cause) {
        super(cause);
    }
}
