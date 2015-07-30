package com.expedia.content.migration.cassandra;

import com.expedia.content.migration.cassandra.exceptions.MigrationUnsuccessfulException;
import com.expedia.content.migration.cassandra.operations.CassandraOperation;
import com.expedia.content.migration.cassandra.operations.ResultType;
import com.expedia.content.migration.cassandra.util.PokeLogger;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@SpringBootApplication
public class Application implements CommandLineRunner, ApplicationContextAware {

    private ApplicationContext appContext;
    private ResultType result;

    @Value("${rollback.enabled}")
    private String rollbackEnabled;

    @Autowired
    private CassandraOperation cassandraOperation;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        PokeLogger.info("Starting the Cassandra Migration Tool.");

        try {
            if (cassandraOperation.performMigration() == ResultType.FAILURE) {
                if (Boolean.parseBoolean(rollbackEnabled)) {
                    result = cassandraOperation.performRollback();

                    if (result == ResultType.SUCCESS) {
                        PokeLogger.info("Rollback operation successfully completed.");
                    } else {
                        PokeLogger.info("Rollback failed.");
                    }

                    throw new MigrationUnsuccessfulException("Migration process failed. Rollback operation performed.");
                } else {
                    throw new MigrationUnsuccessfulException("Migration process failed. No rollback performed since rollback is disabled.");
                }
            } else {
                PokeLogger.info("Migration operation successfully completed.");
            }

        } finally {
            SpringApplication.exit(appContext, result);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        appContext = applicationContext;
    }
}
