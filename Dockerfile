FROM gradle:8-jdk21 AS build
WORKDIR /app

# Copy build files for cache dependence
COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle ./gradle

COPY auth-service/build.gradle.kts ./auth-service/
COPY gateway-service/build.gradle.kts ./gateway-service/

RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon

COPY auth-service/src ./auth-service/src
COPY gateway-service/src ./gateway-service/src

ARG SERVICE_NAME=auth-service

# Build jar-file (skip tests for high speed)
RUN ./gradlew :${SERVICE_NAME}:bootJar -x test --no-daemon

# Run project
FROM eclipse-temurin:21-jre
WORKDIR /app

ARG SERVICE_NAME=auth-service

# Copy builded jar-file an build/libs
COPY --from=build /app/${SERVICE_NAME}/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]