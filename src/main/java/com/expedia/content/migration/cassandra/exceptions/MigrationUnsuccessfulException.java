package com.expedia.content.migration.cassandra.exceptions;

public class MigrationUnsuccessfulException extends RuntimeException {

    private static final long serialVersionUID = 8797607344108908L;

    public MigrationUnsuccessfulException() {
    }

    public MigrationUnsuccessfulException(String message) {
        super(message);
    }

}
