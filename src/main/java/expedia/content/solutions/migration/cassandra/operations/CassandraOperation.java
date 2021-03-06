package expedia.content.solutions.migration.cassandra.operations;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import expedia.content.solutions.migration.cassandra.util.CassandraDao;
import expedia.content.solutions.migration.cassandra.util.CassandraQueryParser;
import expedia.content.solutions.migration.cassandra.util.PokeLogger;

@Component
public class CassandraOperation {

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
        PokeLogger.info("Performing migration operation.");

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
        PokeLogger.error(OperationType.ROLLBACK.toString(), "Performing rollback operation.");

        QueryCommand queries = queryParser.getQueryOperations(OperationType.ROLLBACK);
        return cassandraDao.executeQueryCommand(queries, OperationType.ROLLBACK);
    }

}
