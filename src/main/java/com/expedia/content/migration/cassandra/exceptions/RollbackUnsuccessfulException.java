package com.expedia.content.migration.cassandra.exceptions;

public class RollbackUnsuccessfulException extends RuntimeException {

    private static final long serialVersionUID = 8797607349908908L;

    public RollbackUnsuccessfulException() {
    }

    public RollbackUnsuccessfulException(String message) {
        super(message);
    }

}
