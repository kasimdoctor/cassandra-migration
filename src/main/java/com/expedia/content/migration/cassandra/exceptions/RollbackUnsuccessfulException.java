package com.expedia.content.migration.cassandra.exceptions;

@SuppressWarnings("serial")
public class RollbackUnsuccessfulException extends RuntimeException {

    public RollbackUnsuccessfulException() {
    }

    public RollbackUnsuccessfulException(String message) {
        super(message);
    }

}
