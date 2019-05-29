## Building native image:

Update native-image configs: 
```bash 
java -agentlib:native-image-agent=config-merge-dir=src/main/resources/META-INF/native-image/ -jar target/bq-1.0-SNAPSHOT.jar
```

Build:
```bash
mvn clean package
native-image --no-server --initialize-at-build-time=io.bootique.command.Command -jar target/bq-1.0-SNAPSHOT.jar
```