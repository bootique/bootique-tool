#
# Build stage
#
FROM maven:3.6.3-jdk-{{java.version}}-slim AS build
COPY src /tmp/{{project.name}}/src
COPY pom.xml /tmp/{{project.name}}
COPY assembly.xml /tmp/{{project.name}}
RUN mvn -f /tmp/{{project.name}}/pom.xml clean package

#
# Package stage
#
FROM openjdk:{{java.version}}-jdk-slim
COPY --from=build /tmp/{{project.name}}/target/lib /{{project.name}}/lib
COPY --from=build /tmp/{{project.name}}/target/{{project.name}}-{{project.version}}.jar /{{project.name}}/{{project.name}}-{{project.version}}.jar
ENTRYPOINT ["java","-jar","/{{project.name}}/{{project.name}}-{{project.version}}.jar"]

