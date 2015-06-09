package com.expedia.content.migration.cassandra.operations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.expedia.content.migration.cassandra.exceptions.RollbackUnsuccessfulException;
import com.expedia.content.migration.cassandra.util.CassandraDao;
import com.expedia.content.migration.cassandra.util.CassandraQueryParser;

public class CassandraOperationTest {

    private static final OperationType MIGRATION = OperationType.MIGRATION;
    private static final OperationType ROLLBACK = OperationType.ROLLBACK;

    private CassandraOperation cassandraOperation;

    @Mock
    private CassandraQueryParser queryParser;
    @Mock
    private CassandraDao cassandraDao;

    private QueryCommand queries;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        cassandraOperation = new CassandraOperation(queryParser, cassandraDao);
        queries = new QueryCommand(Arrays.asList("use LodgingDirectory;"));
    }

    @Test
    public void testPerformMigrationSuccessPath() throws IOException {

        when(queryParser.getQueryOperations(MIGRATION)).thenReturn(queries);
        when(cassandraDao.executeQueryCommand(queries, MIGRATION)).thenReturn(ResultType.SUCCESS);

        cassandraOperation.performMigration();

        verify(queryParser).getQueryOperations(MIGRATION);
        verify(cassandraDao).executeQueryCommand(queries, MIGRATION);
    }

    @Test
    public void testPerformMigrationFailurePath() throws IOException {

        when(queryParser.getQueryOperations(MIGRATION)).thenReturn(queries);
        when(queryParser.getQueryOperations(ROLLBACK)).thenReturn(queries);
        when(cassandraDao.executeQueryCommand(queries, MIGRATION)).thenReturn(ResultType.FAILURE);
        when(cassandraDao.executeQueryCommand(queries, ROLLBACK)).thenReturn(ResultType.SUCCESS);

        cassandraOperation.performMigration();

        verify(queryParser).getQueryOperations(MIGRATION);
        verify(cassandraDao).executeQueryCommand(queries, MIGRATION);

        verify(queryParser).getQueryOperations(ROLLBACK);
        verify(cassandraDao).executeQueryCommand(queries, ROLLBACK);
    }

    @Test(expected = RollbackUnsuccessfulException.class)
    public void testPerformRollbackFailurePath() throws IOException {

        when(queryParser.getQueryOperations(MIGRATION)).thenReturn(queries);
        when(queryParser.getQueryOperations(ROLLBACK)).thenReturn(queries);
        when(cassandraDao.executeQueryCommand(queries, MIGRATION)).thenReturn(ResultType.FAILURE);
        when(cassandraDao.executeQueryCommand(queries, ROLLBACK)).thenReturn(ResultType.FAILURE);

        cassandraOperation.performMigration();

        verify(queryParser).getQueryOperations(MIGRATION);
        verify(cassandraDao).executeQueryCommand(queries, MIGRATION);

        verify(queryParser).getQueryOperations(ROLLBACK);
        verify(cassandraDao).executeQueryCommand(queries, ROLLBACK);

    }
}
