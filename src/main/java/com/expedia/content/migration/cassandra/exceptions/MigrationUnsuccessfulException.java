package com.expedia.content.migration.cassandra.exceptions;

@SuppressWarnings("serial")
public class MigrationUnsuccessfulException extends RuntimeException {

    public MigrationUnsuccessfulException() {
    }

    public MigrationUnsuccessfulException(String message) {
        super(message);
    }

}
