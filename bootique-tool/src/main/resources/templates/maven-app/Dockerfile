# Use an image Mave as a parent image
FROM maven:3.6.3-jdk-{{java.version}}-slim AS build

# Set the working directory
WORKDIR /tmp/{{project.name}}

# Copy the pom.xml
COPY pom.xml /tmp/{{project.name}}/
{{#packaging.assembly}}
# Copy the assembly.xml
COPY assembly.xml /tmp/{{project.name}}/
{{/packaging.assembly}}
# Download all dependencies: this will be done only if the pom.xml has any changes
RUN mvn verify clean

# Copy source code
COPY src /tmp/{{project.name}}/src
# Сreate a JAR
RUN mvn clean package

# Use an image OpenJDK
FROM openjdk:{{java.version}}-jdk-slim

{{#packaging.assembly}}
# Copy lib from target
COPY --from=build /tmp/{{project.name}}/target/lib /{{project.name}}/lib
{{/packaging.assembly}}
# Copy jar from target
COPY --from=build /tmp/{{project.name}}/target/{{project.name}}-{{project.version}}.jar /{{project.name}}/{{project.name}}-{{project.version}}.jar

# The command to run the jar file
ENTRYPOINT ["java","-jar","/{{project.name}}/{{project.name}}-{{project.version}}.jar"]