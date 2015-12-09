package expedia.content.solutions.migration.cassandra;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import expedia.content.solutions.migration.cassandra.exceptions.MigrationUnsuccessfulException;
import expedia.content.solutions.migration.cassandra.operations.CassandraOperation;
import expedia.content.solutions.migration.cassandra.operations.ResultType;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationTest {

    @InjectMocks
    private Application application = new Application();

    @Mock
    private ApplicationContext appContext;
    @Mock
    private CassandraOperation cassandraOperation;

    private String[] args;

    @Before
    public void before() {
        args = new String[0];
    }

    @Test
    public void testRunMigrationSuccessful() throws Exception {
        when(cassandraOperation.performMigration()).thenReturn(ResultType.SUCCESS);

        application.run(args);

        verify(cassandraOperation).performMigration();
        verify(cassandraOperation, never()).performRollback();
    }

    @Test(expected = MigrationUnsuccessfulException.class)
    public void testRunMigrationFailure() throws Exception {
        when(cassandraOperation.performMigration()).thenReturn(ResultType.FAILURE);

        application.run(args);

        verify(cassandraOperation).performMigration();
        verify(cassandraOperation).performRollback();
    }

}
