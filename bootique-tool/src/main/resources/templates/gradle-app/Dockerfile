#
# Build stage
#
FROM gradle:6.2.2-jdk{{java.version}} AS build
COPY src /home/app/src
COPY build.gradle /home/app
COPY settings.gradle /home/app
RUN gradle -b /home/app/build.gradle build

#
# Package stage
#
FROM openjdk:{{java.version}}-jdk-slim
COPY --from=build /home/app/build/distributions/{{project.name}}-{{project.version}}.tar /usr/local
RUN tar -xf /usr/local/{{project.name}}-{{project.version}}.tar
ENTRYPOINT ["./{{project.name}}-{{project.version}}/bin/{{project.name}}"]