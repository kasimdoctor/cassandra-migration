package com.expedia.content.migration.cassandra.operations;

import java.util.List;

public class QueryCommand {

    private final List<String> queries;

    public QueryCommand(List<String> queries) {
        this.queries = queries;
    }

    public List<String> getQueriesToExecute() {
        return queries;
    }
}
