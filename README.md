### Cassandra Migration

A Spring application that automates the Cassandra IPU process.  

###### Jenkins Status
- Develop Pipeline: [![Build Status](http://jenkins.karmalab.net/jenkins/job/Cassandra-Migration.DEV_LANE.build/badge/icon)](http://jenkins.karmalab.net/jenkins/job/Cassandra-Migration.DEV_LANE.build/)

___

### Usage

The application is an executable jar (Spring Boot) which can be downloaded from Nexus and incorporated into your project.

* It expects 2 files viz. <b>migration.cql</b> and <b>rollback.cql</b> containing CQL queries to be executed on the Cassandra dB.
* These files can reside in any folder on the server that you are deploying your applications to, and whose paths <b><i>must</i></b> be provided as command line arguments in your ansible scripts, a template for which is described below.
* The application first reads the <b>migration.cql</b> file, parses it removing any comments and executes the queries inside this file. If something should fail due to incorrect query syntax or a Cassandra timeout issue etc., the application then looks for the <b>rollback.cql</b> file as specified by the comand line path, executes the provided rollback operations and then fails signaling that migration has indeed failed.
* In case the <b>rollback.cql</b> is missing or is itself error prone, the application fails right away and deployment fails.


### 2 ways of using the tool: 

#### 1. Running the jar directly

* Since, it's an executable jar, you can run it directly from wherever you want. All you need to do is pass it the arguments it <i>absolutely</i> requires, in the `java -jar` command when you run it.

* Here are the arguments that it requires:

``` 
cassandra.cluster.ips
cassandra.cluster.name
cassandra.datacenter.name
logging.config                 # Optional
migration.script
rollback.script                # Optional
```

e.g.

```Java
java -jar cassandra-migration.jar --migration.script="PATH TO MIGRATION SCRIPT" --cassandra.cluster.ips="<A COMMA SEPARATED LIST OF CASSANDRA IPs>" --cassandra.cluster.name="<CLUSTER NAME>" --cassandra.datacenter.name="<DC NAME>"  
```



#### 2. Ansible Template to deploy Cassandra Migration

You will need a structure similar to the following if you use Ansible

![Ansible](/docs/ansible_structure.PNG? "Ansible structure")

The above screenshot is taken from the <b>[3rd-Party-Content-Acquisition](https://ewegithub.sb.karmalab.net/ContentSystems/3rdparty-content-acquisition)</b> repository and it shows a structure that is used to deploy the cassandra-migration.jar  

* This is the main.yml from under the <b><i>defaults</i></b> folder :
 
```yml
cassandraMigrationJavaHome: '{{ javaHome }}'
cassandraMigrationArtifactFileName: '{{ cassandraMigrationInstanceName }}.jar'

cassandraMigrationUser: 'tomcat'
cassandraMigrationGroup: 'software'

# NOTE: You can configure any path you like for the BasePath here. This is just an example. 
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

* This is the main.yml file from under the <b><i>meta</i></b> folder :

```yml
---
# Roles that should be installed prior to the current one
dependencies:
  - java

```

From the above yml file, it is important to note that the cassandra-migration jar only depends on Java and thus no other prerequisites are needed.  


* This is the main.yml file from under the <b><i>tasks</i></b> folder :

```yml

---
# This role installs the Cassandra-Migration module

- name: Create the Cassandra-Migration OS user group
  group: name={{ cassandraMigrationGroup }}

- name: Create the Cassandra-Migration OS user
  user: name={{ cassandraMigrationUser }} group={{ cassandraMigrationGroup }}

- name: Delete existing Cassandra-Migration files
  sudo: yes
  file: path={{ cassandraMigrationAppPath }} state=absent

- name: Create Cassandra-Migration directories
  file: path={{ item }} state=directory owner={{ cassandraMigrationUser }} group={{ cassandraMigrationGroup }} mode=0755 recurse=yes
  with_items:
    - "{{ cassandraMigrationAppPath }}"                   
    - "{{ cassandraMigrationConfigPath }}"
    - "{{ cassandraMigrationAppPath }}/lib"
    - "{{ cassandraMigrationLogPath }}/"    

- name: Get the jar from the  Nexus repository  
  get_url: url={{ cassandraMigration_jar_url }} dest={{ cassandraMigrationAppPath }}/lib/{{ cassandraMigrationInstanceName }}.jar force="yes" owner={{ cassandraMigrationUser }} group={{ cassandraMigrationGroup }} mode=0755

- name: Create Cassandra-Migration configuration files
  template: src={{ item }} dest={{ cassandraMigrationConfigPath }}/ owner={{ cassandraMigrationUser }} group={{ cassandraMigrationGroup }}
  with_items:
    - application.properties
    - logback.xml
    
- name: Change Cassandra-Migration files ownership and permissions
  file: path={{ cassandraMigrationAppPath }} recurse=yes owner={{ cassandraMigrationUser }} group={{ cassandraMigrationGroup }} mode=755


# ADD STEP TO COPY migration.cql and rollback.cql FILES TO CUSTOM DIR HERE.


- name: Start the Cassandra-Migration application
  shell: su {{ user }} -c 'cd {{ cassandraMigrationAppPath }}; {{ javaHome }}/bin/java -jar lib/cassandra-migration.jar --migration.script="PATH TO MIGRATION SCRIPT" --rollback.script="PATH TO ROLLBACK SCRIPT" '

```

From the above yml file, please take note that you will have to specify these variables in <b>all.yml</b> or equivalent to download the jar from Nexus:

```yml

# Nexus Repositories
nexus_repository_url: 'http://nexus.sb.karmalab.net/nexus'
nexus_repository: 'cs-releases'

# Cassandra Migration Tool
cassandraMigration_jar_url: '{{ nexus_repository_url }}/service/local/artifact/maven/content?r={{ nexus_repository }}&g=com.expedia.content.migration&a=cassandra-migration&e=jar&v=LATEST '


```  
  
* Finally, copy the following application.properties file verbatim and put it under the templates or equivalent folder.  
  <b>N.B :</b> Make sure you have the variables used below i,e.(<i>cassandra_cluster_ips</i> etc.) defined somewhere in your                   project.

```yaml

[Cassandra]
cassandra.cluster.ips={{ cassandra_cluster_ips }}
cassandra.cluster.name={{ cassandra_cluster_name }}
cassandra.datacenter.name={{ cassandra_datacenter_name }}

[Logback]
logging.config={{ cassandraMigrationConfigPath }}/logback.xml

[Scripts]
migration.script=
rollback.script=

```  


**In the end, all you need to do is create a playbook which deploys the above created role and have it run as the first step in deploying your application.**  

#### Further Reading
**Ansible for Dummies :**

* [Creating Ansible Roles](http://www.azavea.com/blogs/labs/2014/10/creating-ansible-roles-from-scratch-part-1/)
* [YAML Syntax](https://docs.ansible.com/YAMLSyntax.html)
