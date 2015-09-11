package expedia.content.solutions.migration.cassandra.operations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import expedia.content.solutions.migration.cassandra.util.CassandraDao;
import expedia.content.solutions.migration.cassandra.util.CassandraQueryParser;

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
    public void testPerformMigration() throws IOException {
        when(queryParser.getQueryOperations(MIGRATION)).thenReturn(queries);
        when(cassandraDao.executeQueryCommand(queries, MIGRATION)).thenReturn(ResultType.SUCCESS);

        ResultType result = cassandraOperation.performMigration();

        verify(queryParser).getQueryOperations(MIGRATION);
        verify(cassandraDao).executeQueryCommand(queries, MIGRATION);
        assertThat(result).isEqualTo(ResultType.SUCCESS);

        verify(queryParser, never()).getQueryOperations(ROLLBACK);
        verify(cassandraDao, never()).executeQueryCommand(queries, ROLLBACK);
    }

    @Test
    public void testPerformRollback() throws IOException {
        when(queryParser.getQueryOperations(ROLLBACK)).thenReturn(queries);
        when(cassandraDao.executeQueryCommand(queries, ROLLBACK)).thenReturn(ResultType.FAILURE);

        ResultType result = cassandraOperation.performRollback();

        verify(queryParser).getQueryOperations(ROLLBACK);
        verify(cassandraDao).executeQueryCommand(queries, ROLLBACK);

        verify(queryParser, never()).getQueryOperations(MIGRATION);
        verify(cassandraDao, never()).executeQueryCommand(queries, MIGRATION);
        assertThat(result).isEqualTo(ResultType.FAILURE);
    }

}
