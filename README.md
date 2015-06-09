### Cassandra Migration

A Spring application that automates the Cassandra IPU process.
___

### Usage

The application is an executable jar (a Spring boot app) which can be downloaded from Nexus and incorporated into your project.

* It expects 2 files viz. <b>migration.cql</b> and <b>rollback.cql</b> containing CQL queries to be executed on the Cassandra dB.
* These files can reside in any folder on the server that you are deploying your applications to, and whose paths <b><i>must</i></b> be provided as command line arguments in your ansible   scripts, a template for which is described below.
* The application first reads the <b>migration.cql</b> file, parses it removing any comments and executes the queries inside this file. If something should fail due to incorrect query syntax or a Cassandra timeout issue etc., the application then looks for the <b>rollback.cql</b> file as specified by the comand line path and executes the provided rollback operations.
* In case the <b>rollback.cql</b> is missing or is itself error prone, the application fails and deployment fails.


#### Ansible Template to deploy Cassandra Migration

You will need a structure similar to the following if you use Ansible

![Ansible](/docs/ansible_structure.PNG? "Optional Title")

The above screenshot is taken from 3rd-Party-Content-Acquisition repo and it shows a structure that is used to deploy the cassandra-migration.jar

* This is the main.yml from under the <i>defaults</i> folder :
 
```
cassandraMigrationJavaHome: '{{ javaHome }}'
cassandraMigrationArtifactFileName: '{{ cassandraMigrationInstanceName }}.jar'

cassandraMigrationUser: 'tomcat'
cassandraMigrationGroup: 'software'

cassandraMigrationBasePath: '/opt/expedia' 
cassandraMigrationInstanceName: 'cassandra-migration'
cassandraMigrationAppPath: '{{ cassandraMigrationBasePath }}/{{ cassandraMigrationInstanceName }}'
cassandraMigrationConfigPath: '{{ cassandraMigrationAppPath }}/config'
cassandraMigrationLogPath: '/opt/logs/{{ cassandraMigrationInstanceName }}'

# JVM params
# Ports should not be within the ranges:
# from 32768 to 61000 : Reserved for Linux (/proc/sys/net/ipv4/ip_local_port_range)
# from 31000 to 31999 : Reserved for Wrapper (in 3rdparty we override the default range)
cassandraMigrationJmxPort: 22132
cassandraMigrationInitMemory: 64m
cassandraMigrationMaxMemory: 256m

instanceName: "{{ cassandraMigrationInstanceName }}"

```

* This is the main.yml file from the <i>meta</i> folder :

```
---
# Roles that should be installed prior to the current one
dependencies:
  - java

```

From the above yml file, it is important to note that the cassandra-migration jar only depends on Java and thus no other prerequisites are needed.




