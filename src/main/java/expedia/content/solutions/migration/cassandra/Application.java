package expedia.content.solutions.migration.cassandra;

import expedia.content.solutions.migration.cassandra.exceptions.MigrationUnsuccessfulException;
import expedia.content.solutions.migration.cassandra.operations.CassandraOperation;
import expedia.content.solutions.migration.cassandra.operations.ResultType;
import expedia.content.solutions.migration.cassandra.util.PokeLogger;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@SpringBootApplication
public class Application implements CommandLineRunner, ApplicationContextAware {

    private ApplicationContext appContext;
    private ResultType result;

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
                result = cassandraOperation.performRollback();

                if (result == ResultType.SUCCESS) {
                    PokeLogger.info("Rollback operation successfully completed.");
                } else {
                    PokeLogger.info("Rollback failed.");
                }

                throw new MigrationUnsuccessfulException("Migration process failed. Rollback operation performed.");
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
