### Cassandra Migration

A Spring application that automates the Cassandra IPU process.
___

### Usage

The application is an executable jar (a Spring boot app) which can be downloaded from Nexus and incorporated into your project.

* It expects 2 files viz. <b>migration.cql</b> and <b>rollback.cql</b> containing CQL queries to be executed on the Cassandra dB.
* These files can reside in any folder on the server, whose paths <b><i>must</i></b> be provided as command line arguments in your ansible   scripts, a template for which is described below.
* The application first reads the <b>migration.cql</b> file, parses it removing any comments and executes the queries inside this file. If something should fail due to incorrect query syntax or a Cassandra timeout issue etc., the application then looks for the <b>rollback.cql</b> file as specified by the comand line path and executes the provided rollback operations.
* In case the <b>rollback.cql</b> is missing or is itself error prone, the application fails and deployment fails.


#### Ansible Template to deploy Cassandra Migration

You will need 
