# deploy-jboss-plugin

[Jenkins](https://jenkins-ci.org/) plugin to run deploy artifacts to JBoss AS and JBoss EAP in Domain and Standalone mode.

![alt tag](https://raw.github.com/Daniel69/deploy-jboss-plugin/raw/master/screnshot.png)


#### Compilation

 * `git clone git@github.com:Daniel69/deploy-jboss-plugin.git`
 * `cd deploy-jboss-plugin/`
 * `mvn clean install -DskipTests=true`

#### Installation

Assuming Jenkins runs on `http://localhost:8080/`:
 * `wget http://localhost:8080/jnlpJars/jenkins-cli.jar`
 * `java -jar jenkins-cli.jar -s http://localhost:8080/ install-plugin ./target/deploy-jboss.hpi -restart`

Alternatively:
 * Go to `http://localhost:8080/pluginManager/advanced`
 * Upload `./target/deploy-jboss.hpi`
 * Restart Jenkins

#### Usage
 * When configuring a project, add a post build action and configure.

#### Plugin Dependencies
* None
