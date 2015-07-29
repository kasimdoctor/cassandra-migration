package com.expedia.content.migration.cassandra;

import static org.mockito.Mockito.verify;

import com.expedia.content.migration.cassandra.operations.CassandraOperation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

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
    public void testRun() throws Exception {
        application.run(args);
        verify(cassandraOperation).performMigration();
    }

}
