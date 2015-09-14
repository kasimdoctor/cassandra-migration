package expedia.content.solutions.migration.cassandra.operations;

import org.springframework.boot.ExitCodeGenerator;

public enum ResultType implements ExitCodeGenerator {

    SUCCESS(0),
    FAILURE(1);

    private int exitCode;

    private ResultType(int value) {
        this.exitCode = value;
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}
