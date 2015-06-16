package com.expedia.content.migration.cassandra;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.annotation.PostConstruct;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.expedia.cs.poke.client.Poke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfiguration.class);

    @Value("${cassandra.cluster.name}")
    private String clusterName;

    @Value("#{'${cassandra.cluster.ips}'.split(',')}")
    private String[] ips;

    @Value("${cassandra.datacenter.name}")
    private String dataCenter;

    @Value("${pokeEnabled}")
    private String enabled;

    @Value("${pokeTo}")
    private String pokeTo;

    @Value("${pokeUrl}")
    private String pokeUrl;

    @Bean
    public Cluster cassandraCluster() {
        return Cluster.builder().addContactPoints(ips).withClusterName(clusterName).withLoadBalancingPolicy(new DCAwareRoundRobinPolicy(dataCenter))
                .build();
    }

    @Bean
    public Session cassandraLodgingDirectorySession(Cluster cluster) {
        return cluster.connect();
    }

    @PostConstruct
    public void initializePoke() throws UnknownHostException {
        LOGGER.info("################## " + pokeTo + "############### " + pokeUrl + "######### " + Boolean.parseBoolean(enabled));
        final String INSTANCE_NAME = System.getProperty("user.name") + "@" + InetAddress.getLocalHost().getHostName();
        Poke.init(Boolean.parseBoolean(enabled), pokeUrl, pokeTo, INSTANCE_NAME);
    }
}
