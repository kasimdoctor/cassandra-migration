package com.expedia.content.migration.cassandra.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.expedia.content.migration.cassandra.operations.OperationType;
import com.expedia.content.migration.cassandra.operations.QueryCommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

/**
 * A parser for CQL files.
 */
@Component
public class CassandraQueryParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraQueryParser.class);

    private static final String SEMI_COLON = ";";
    private static final String SPACE = " ";
    private static final String BLOCK_COMMENT_START = "/*";
    private static final String BLOCK_COMMENT_END = "*/";

    @Value("${migration.script}")
    private String migrationFilePath;

    @Value("${rollback.script}")
    private String rollbackFilePath;

    private ResourceLoader loader;

    @Autowired
    public CassandraQueryParser(ResourceLoader loader) {
        this.loader = loader;
    }

    /**
     * Parses the specific CQL file based on the {@code OperationType} and returns a {@code QueryCommand} object
     * that contains the queries to be executed
     * 
     * @param type the type of operation to be performed
     * @return a {@code QueryCommand} that has the parsed queries
     * @throws IOException
     */
    public QueryCommand getQueryOperations(OperationType type) throws IOException {

        Resource resource = null;
        switch (type) {
            case MIGRATION:
                LOGGER.info("Starting to parse the migration query file.");
                resource = loader.getResource("file:" + migrationFilePath);
                break;

            case ROLLBACK:
                LOGGER.info("Starting to parse the rollback query file.");
                resource = loader.getResource("file:" + rollbackFilePath);
                break;

            default:
                LOGGER.error("Unsupported operation type encountered by query parser.");

        }

        return new QueryCommand(parseCassandraQueryFile(resource));
    }

    private List<String> parseCassandraQueryFile(Resource resource) throws IOException {
        List<String> queries = new ArrayList<>();
        StringBuilder query = new StringBuilder();

        InputStream inputStream = resource.getInputStream();
        try (Scanner scanner = new Scanner(inputStream)) {
            String line;
            boolean commentFlag = false;
            while (scanner.hasNext()) {
                line = scanner.nextLine().trim();

                if (line.startsWith(BLOCK_COMMENT_START) && line.endsWith(BLOCK_COMMENT_END)) {
                    continue;
                } else if (line.startsWith(BLOCK_COMMENT_START)) {
                    commentFlag = true;
                } else if (line.endsWith(BLOCK_COMMENT_END)) {
                    commentFlag = false;
                    continue;
                }

                if (!commentFlag) {
                    if (!line.endsWith(SEMI_COLON)) {
                        query.append(line).append(SPACE);
                    } else {
                        query.append(line);
                        queries.add(query.toString());
                        query = new StringBuilder();
                    }
                }
            }
        }

        if (queries.isEmpty()) {
            LOGGER.warn("The file={} does not contain any executable Cassandra queries", resource.getFilename());
        }
        return queries;
    }

}
