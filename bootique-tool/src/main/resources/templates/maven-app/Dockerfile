#
# Build stage
#
FROM maven:3.6.3-jdk-{{java.version}}-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
COPY assembly.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

#
# Package stage
#
FROM openjdk:{{java.version}}-jdk-slim
COPY --from=build /home/app/target/lib /usr/local/lib
COPY --from=build /home/app/target/{{project.name}}-{{project.version}}.jar /usr/local/app.jar
ENTRYPOINT ["java","-jar","/usr/local/app.jar"]

