package com.expedia.content.migration.cassandra.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.datastax.driver.core.ExecutionInfo;
import com.datastax.driver.core.QueryTrace;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SimpleStatement;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.exceptions.InvalidQueryException;
import com.expedia.content.migration.cassandra.operations.OperationType;
import com.expedia.content.migration.cassandra.operations.QueryCommand;
import com.expedia.content.migration.cassandra.operations.ResultType;

@RunWith(MockitoJUnitRunner.class)
public class CassandraDaoTest {

    private static final OperationType MIGRATION = OperationType.MIGRATION;

    private CassandraDao cassandraDao;

    @Mock
    private Session session;
    @Mock
    private ResultSet rs;
    @Mock
    private ExecutionInfo execInfo;
    @Mock
    private QueryCommand queryCommand;
    @Mock
    private SimpleStatement statement;
    @Mock
    private QueryTrace queryTrace;

    @Before
    public void before() {
        when(queryCommand.getQueriesToExecute()).thenReturn(Arrays.asList("use Lodgingdirectory;", "drop table property;"));
        cassandraDao = new CassandraDao(session);
    }

    @Test
    public void testExecuteQueryCommandSuccessPath() {

        when(session.execute(any(Statement.class))).thenReturn(rs);
        when(rs.getExecutionInfo()).thenReturn(execInfo);
        when(execInfo.getQueryTrace()).thenReturn(queryTrace);

        ResultType result = cassandraDao.executeQueryCommand(queryCommand, MIGRATION);

        verify(session, times(2)).execute(any(Statement.class));
        verify(execInfo, times(2)).getQueryTrace();
        assertThat(result).isEqualTo(ResultType.SUCCESS);
    }

    @Test
    public void testExecuteQueryCommandFailPath() {

        when(session.execute(any(Statement.class))).thenThrow(new InvalidQueryException("Invalid query"));

        ResultType result = cassandraDao.executeQueryCommand(queryCommand, MIGRATION);

        verify(session, times(1)).execute(any(Statement.class));
        verify(execInfo, never()).getQueryTrace();
        assertThat(result).isEqualTo(ResultType.FAILURE);
    }

}
