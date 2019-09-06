<!--
  Licensed to ObjectStyle LLC under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ObjectStyle LLC licenses
  this file to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
  -->

## Building native image

#### Prerequisites

Install [GraalVM](https://github.com/oracle/graal/releases) and set _JAVA_HOME_ to use it.

#### Maven
```bash
mvn clean package -Pnative-image
```

#### Manual
```bash
native-image --no-server --report-unsupported-elements-at-runtime --no-fallback \
             --initialize-at-build-time=io.bootique.tools.shell.command.ShellCommand \
             --initialize-at-build-time=io.bootique.command.Command \
             -H:Name=bq -jar target/bq-1.0-SNAPSHOT.jar
```

## Update native-image configs
 
```bash 
java -agentlib:native-image-agent=config-merge-dir=src/main/resources/META-INF/native-image/ -jar target/bq-1.0-SNAPSHOT.jar
```

## How to deploy

```bash 
mvn release:prepare -DignoreSnapshots=true -Dtag=TAG_SAME_WITH_RELEASE_VERSION -DreleaseVersion=RELEASE_VERSION -DdevelopmentVersion=NEXT_DEV_VERSION
mvn release:perform -Darguments="-Dmaven.deploy.skip=true"
```
