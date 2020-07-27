# Use an image Gradle as a parent image
FROM gradle:6.5.1-jdk{{java.version}} AS build

# Set the working directory
WORKDIR /tmp/{{project.name}}/

# Copy the gradle
COPY gradle /tmp/{{project.name}}/gradle
# Copy the build.gradle snf the settings.gradle
COPY build.gradle settings.gradle /tmp/{{project.name}}/
# Copy the gradlew
COPY gradlew /tmp/{{project.name}}/gradlew
# Copy source code
COPY src /tmp/{{project.name}}/src

# Build a project
RUN chmod +x gradlew && ./gradlew build

# Use an image OpenJDK
FROM openjdk:{{java.version}}-jdk-slim

# Copy tar from build/distributions
COPY --from=build /tmp/{{project.name}}/build/distributions/{{project.name}}-{{project.version}}.tar /
# Unpacking {{project.name}}-{{project.version}}
RUN tar -xf /{{project.name}}-{{project.version}}.tar && rm /{{project.name}}-{{project.version}}.tar

# The command to run the project
ENTRYPOINT ["/{{project.name}}-{{project.version}}/bin/{{project.name}}"]