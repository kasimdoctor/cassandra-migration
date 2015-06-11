package com.expedia.content.migration.cassandra;

import com.expedia.content.migration.cassandra.exceptions.MigrationUnsuccessfulException;
import com.expedia.content.migration.cassandra.operations.CassandraOperation;
import com.expedia.content.migration.cassandra.operations.ResultType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@SpringBootApplication
public class Application implements CommandLineRunner, ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    private ApplicationContext appContext;
    private ResultType result;

    @Autowired
    private CassandraOperation cassandraOperation;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("Starting the Cassandra Sitesup Operation.");

        try {
            if (cassandraOperation.performMigration() == ResultType.FAILURE) {
                result = cassandraOperation.performRollback();
                throw new MigrationUnsuccessfulException("Migration process failed. Rollback operation performed.");
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
