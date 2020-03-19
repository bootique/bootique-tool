#
# Build stage
#
FROM openjdk:{{java.version}}-jdk-slim AS build
COPY src /tmp/{{project.name}}/src
COPY gradle /tmp/{{project.name}}/gradle
COPY build.gradle /tmp/{{project.name}}
COPY settings.gradle /tmp/{{project.name}}
COPY gradlew /tmp/{{project.name}}/gradlew

WORKDIR /tmp/{{project.name}}/
RUN chmod +x gradlew
RUN ./gradlew build

#
# Package stage
#
FROM openjdk:{{java.version}}-jdk-slim
COPY --from=build /tmp/{{project.name}}/build/distributions/{{project.name}}-{{project.version}}.tar /
RUN tar -xf /{{project.name}}-{{project.version}}.tar
RUN rm /{{project.name}}-{{project.version}}.tar
ENTRYPOINT ["/{{project.name}}-{{project.version}}/bin/{{project.name}}"]