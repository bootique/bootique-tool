## Building native image:
 
```bash 
java -agentlib:native-image-agent=config-merge-dir=src/main/resources/META-INF/native-image/ \ 
    -jar target/bq-1.0-SNAPSHOT.jar
```

```bash
$JAVA_HOME/bin/native-image -jar target/bq-1.0-SNAPSHOT.jar
```