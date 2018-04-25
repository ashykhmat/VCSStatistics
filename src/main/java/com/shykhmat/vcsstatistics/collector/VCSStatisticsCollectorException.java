package com.shykhmat.vcsstatistics.collector;

/**
 * Exception that occurs during project commit statistics calculation.
 */
public class VCSStatisticsCollectorException extends Exception {
    private static final long serialVersionUID = 6798925019132143038L;

    public VCSStatisticsCollectorException(Throwable cause) {
        super(cause);
    }

    public VCSStatisticsCollectorException(String message) {
        super(message);
    }
}
