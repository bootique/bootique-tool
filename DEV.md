## Building native image

#### Prerequisites

Install [GraalVM](https://github.com/oracle/graal/releases) and set _JAVA_HOME_ to use it.

#### Maven
```bash
mvn clean package
```

#### Manual
```bash
native-image --no-server --report-unsupported-elements-at-runtime --no-fallback \
             --initialize-at-build-time=io.bootique.command.Command -jar target/bq-1.0-SNAPSHOT.jar
```

## Update native-image configs
 
```bash 
java -agentlib:native-image-agent=config-merge-dir=src/main/resources/META-INF/native-image/ -jar target/bq-1.0-SNAPSHOT.jar
```
