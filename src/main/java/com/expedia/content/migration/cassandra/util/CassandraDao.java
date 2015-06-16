package com.expedia.content.migration.cassandra.util;

import java.util.List;

import com.datastax.driver.core.ExecutionInfo;
import com.datastax.driver.core.QueryTrace;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SimpleStatement;
import com.datastax.driver.core.Statement;
import com.expedia.content.migration.cassandra.operations.OperationType;
import com.expedia.content.migration.cassandra.operations.QueryCommand;
import com.expedia.content.migration.cassandra.operations.ResultType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CassandraDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraDao.class);

    private PokeLogger pokeLogger = PokeLogger.INSTANCE;
    private Session session;

    @Autowired
    public CassandraDao(Session session) {
        this.session = session;
    }

    public ResultType executeQueryCommand(QueryCommand queries, OperationType operationType) {

        List<String> queriesToExecute = queries.getQueriesToExecute();

        pokeLogger.info(operationType.toString(),
                String.format("Starting execution of count=%s queries for operation type= %s.", queriesToExecute.size(), operationType));
        ResultType result = ResultType.SUCCESS;
        try {

            for (String query : queriesToExecute) {

                Statement statement = new SimpleStatement(query);
                statement.enableTracing();

                LOGGER.info("Executing query={}.", query);
                ExecutionInfo execInfo = session.execute(statement).getExecutionInfo();

                logQueryTrace(execInfo);

            }
        } catch (Exception ex) {
            pokeLogger.error("ERROR: " + operationType.toString(),
                    String.format("Exception encountered while performing operation of type=%s", operationType), ex);
            result = ResultType.FAILURE;
        }

        return result;
    }

    private void logQueryTrace(ExecutionInfo execInfo) {
        QueryTrace trace = execInfo.getQueryTrace();
        LOGGER.info("Cassandra query with trace UUID={} started at startTime={} with requestType={}", trace.getTraceId(), trace.getStartedAt(),
                trace.getRequestType());
    }
}
