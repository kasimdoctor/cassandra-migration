package com.expedia.content.migration.cassandra.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;

import com.expedia.content.migration.cassandra.operations.OperationType;
import com.expedia.content.migration.cassandra.operations.QueryCommand;
import com.expedia.content.migration.cassandra.util.CassandraQueryParser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

@RunWith(MockitoJUnitRunner.class)
public class CassandraQueryParserTest {

    private static final OperationType MIGRATION = OperationType.MIGRATION;
    private static final OperationType ROLLBACK = OperationType.ROLLBACK;
    private static final String MIGRATION_FILE = "classpath:migration.cql";
    private static final String ROLLBACK_FILE = "classpath:rollback.cql";

    private CassandraQueryParser queryParser;

    @Mock
    private ResourceLoader loader;
    @Mock
    private Resource resource;
    @Mock
    private InputStream inputStream;

    @Before
    public void before() {
        queryParser = new CassandraQueryParser(loader);
        when(loader.getResource(any(String.class))).thenReturn(resource);
    }

    @Test
    public void testGetQueryOperationsMigrationPath() throws IOException {
        when(resource.getInputStream()).thenReturn(inputStream);

        QueryCommand queries = queryParser.getQueryOperations(MIGRATION);

        verify(loader).getResource(MIGRATION_FILE);
        verify(loader, never()).getResource(ROLLBACK_FILE);
        assertThat(queries).isNotNull();
    }

    @Test
    public void testGetQueryOperationsRollbackPath() throws IOException {
        when(resource.getInputStream()).thenReturn(inputStream);

        QueryCommand queries = queryParser.getQueryOperations(ROLLBACK);

        verify(loader).getResource(ROLLBACK_FILE);
        verify(loader, never()).getResource(MIGRATION_FILE);
        assertThat(queries).isNotNull();
    }

}
