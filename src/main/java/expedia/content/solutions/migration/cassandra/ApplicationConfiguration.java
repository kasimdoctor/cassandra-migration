package expedia.content.solutions.migration.cassandra;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.annotation.PostConstruct;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.expedia.cs.poke.client.Poke;

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

    @Value("${pokeEnabled}")
    private String enabled;

    @Value("${pokeEmail}")
    private String pokeTo;

    @Value("${pokeServer}")
    private String pokeUrl;

    @Value("${hipchat.room}")
    private String hipchatRoomName;

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
        final String user = System.getProperty("user.name") + "@" + InetAddress.getLocalHost().getHostName();
        Poke.config(Boolean.parseBoolean(enabled), pokeUrl, user, "Cassandra-Migration-Tool").email(pokeTo).hipchat(hipchatRoomName);
    }
}
