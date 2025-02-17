package com.capstone.bgjobs.exception;

/**
 * Custom exception to indicate an issue during Elasticsearch operations.
 */
public class ElasticsearchOperationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ElasticsearchOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ElasticsearchOperationException(String message) {
        super(message);
    }
}
