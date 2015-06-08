package com.expedia.content.migration.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    @Value("${cassandra.cluster.name}")
    private String clusterName;

    @Value("#{'${cassandra.cluster.ips}'.split(',')}")
    private String[] ips;

    @Value("${cassandra.datacenter.name}")
    private String dataCenter;

    @Bean
    public Cluster cassandraCluster() {
        return Cluster.builder().addContactPoints(ips).withClusterName(clusterName).withLoadBalancingPolicy(new DCAwareRoundRobinPolicy(dataCenter))
                .build();
    }

    @Bean
    public Session cassandraLodgingDirectorySession(Cluster cluster) {
        return cluster.connect();
    }

}
