package com.expedia.content.migration.cassandra.operations;

import java.io.IOException;

import com.expedia.content.migration.cassandra.util.CassandraDao;
import com.expedia.content.migration.cassandra.util.CassandraQueryParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CassandraOperation {

    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraOperation.class);

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
    public ResultType performMigration() throws IOException {
        LOGGER.info("Performing migration operation.");

        QueryCommand queries = queryParser.getQueryOperations(OperationType.MIGRATION);
        return cassandraDao.executeQueryCommand(queries, OperationType.MIGRATION);
    }

    /**
     * Performs the rollback operation on the Cassandra dB by executing the queries
     * present in the <b>rollback.cql</b> file
     * 
     * @throws IOException
     */
    public ResultType performRollback() throws IOException {
        LOGGER.info("Performing rollback operation.");

        QueryCommand queries = queryParser.getQueryOperations(OperationType.ROLLBACK);
        return cassandraDao.executeQueryCommand(queries, OperationType.ROLLBACK);
    }

}