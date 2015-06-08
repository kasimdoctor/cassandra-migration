package com.expedia.content.migration.cassandra.operations;

import java.io.IOException;

import com.expedia.content.migration.cassandra.exceptions.RollbackUnsuccessfulException;
import com.expedia.content.migration.cassandra.util.CassandraDao;
import com.expedia.content.migration.cassandra.util.CassandraQueryParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.stereotype.Component;

@Component
public class CassandraOperation implements ExitCodeGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraOperation.class);

    private static OperationType operation;
    private static ResultType result;

    private CassandraQueryParser queryParser;
    private CassandraDao cassandraDao;

    @Autowired
    public CassandraOperation(CassandraQueryParser queryParser, CassandraDao cassandraDao) {
        this.queryParser = queryParser;
        this.cassandraDao = cassandraDao;
    }

    /**
     * Performs the migration operation on the Cassandra dB by executing the queries
     * present in the <b>migration.cql</b> file
     * 
     * @throws IOException
     */
    public void performMigration() throws IOException {
        LOGGER.info("Performing migration operation.");

        operation = OperationType.MIGRATION;
        QueryCommand queries = queryParser.getQueryOperations(operation);
        result = cassandraDao.executeQueryCommand(queries, operation);

        if (result == ResultType.FAILURE) {
            performRollback();
        }

    }

    /**
     * Performs the rollback operation on the Cassandra dB should the migration fail,
     * by executing the queries present in the <b>rollback.cql</b> file
     * 
     * @throws IOException
     */
    public void performRollback() throws IOException {
        LOGGER.info("Performing rollback operation.");

        operation = OperationType.ROLLBACK;
        QueryCommand queries = queryParser.getQueryOperations(operation);
        result = cassandraDao.executeQueryCommand(queries, operation);

        if (result == ResultType.FAILURE) {
            throw new RollbackUnsuccessfulException();
        }

    }

    @Override
    public int getExitCode() {
        return 0;
    }

}
