package expedia.content.solutions.migration.cassandra.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import expedia.content.solutions.migration.cassandra.operations.OperationType;
import expedia.content.solutions.migration.cassandra.operations.QueryCommand;

@RunWith(MockitoJUnitRunner.class)
public class CassandraQueryParserTest {

    private static final OperationType MIGRATION = OperationType.MIGRATION;

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
    public void testGetQueryOperations() throws IOException {
        when(resource.getInputStream()).thenReturn(inputStream);

        QueryCommand queries = queryParser.getQueryOperations(MIGRATION);

        verify(loader).getResource(any(String.class));
        assertThat(queries).isNotNull();
    }

}
