package expedia.content.solutions.migration.cassandra.util;

import java.util.List;

import com.datastax.driver.core.ExecutionInfo;
import com.datastax.driver.core.QueryTrace;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SimpleStatement;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.exceptions.TraceRetrievalException;
import com.expedia.cs.poke.client.Poke;

import expedia.content.solutions.migration.cassandra.operations.OperationType;
import expedia.content.solutions.migration.cassandra.operations.QueryCommand;
import expedia.content.solutions.migration.cassandra.operations.ResultType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CassandraDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraDao.class);

    @Value("${migration.version}")
    private String migrationVersion;

    private Session session;

    @Autowired
    public CassandraDao(Session session) {
        this.session = session;
    }

    public ResultType executeQueryCommand(QueryCommand queries, OperationType operationType) {

        List<String> queriesToExecute = queries.getQueriesToExecute();

        PokeLogger.info(String.format("Starting execution of count=%s queries for operation type= %s.", queriesToExecute.size(), operationType));
        ResultType result = ResultType.SUCCESS;
        int successCount = 0;
        try {

            for (String query : queriesToExecute) {

                Statement statement = new SimpleStatement(query);
                statement.enableTracing();

                LOGGER.info("Executing query={}.", query);
                ExecutionInfo execInfo = session.execute(statement).getExecutionInfo();

                logQueryTrace(execInfo);
                successCount++;

            }
        } catch (Exception ex) {
            PokeLogger.error("ERROR: " + operationType.toString(),
                    String.format("Exception encountered while executing query >  %s", queriesToExecute.get(successCount)), ex);
            result = ResultType.FAILURE;
        }

        if (result == ResultType.SUCCESS) {
            StringBuilder message = new StringBuilder();
            message.append("Executed queries are: \n\n");
            queriesToExecute.stream().forEach(x -> message.append(String.format("%s", ">  " + x.trim() + " \n")));
            Poke.build().email("SUCCESS: " + operationType.toString() + " - " + migrationVersion).poke(message.toString());

        } else {
            StringBuilder message = new StringBuilder();
            message.append("Queries that were NOT executed are: \n\n");
            queriesToExecute.stream().skip(successCount).forEach(x -> message.append(String.format("%s", ">  " + x.trim() + " \n")));
            Poke.build().email("FAILURE: " + operationType.toString() + " - " + migrationVersion).poke(message.toString());
        }

        return result;
    }

    private void logQueryTrace(ExecutionInfo execInfo) {
        try {
            QueryTrace trace = execInfo.getQueryTrace();
            LOGGER.info("Cassandra query with trace UUID={} started at startTime={} with requestType={}", trace.getTraceId(), trace.getStartedAt(),
                    trace.getRequestType());
        } catch (TraceRetrievalException e) {
            LOGGER.info("Query trace for this query could not be retrieved.");
        }

    }
}
