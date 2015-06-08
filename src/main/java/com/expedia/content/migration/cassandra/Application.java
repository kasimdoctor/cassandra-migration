package com.expedia.content.migration.cassandra;

import com.expedia.content.migration.cassandra.operations.CassandraOperation;

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

    @Autowired
    private CassandraOperation cassandraOperation;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("Starting the Cassandra Sitesup Operation.");

        try {
            cassandraOperation.performMigration();
        } finally {
            SpringApplication.exit(appContext, cassandraOperation);
        }

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        appContext = applicationContext;
    }
}
