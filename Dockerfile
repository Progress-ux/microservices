FROM gradle:9.4-jdk21 AS build
WORKDIR /app

ENV GRADLE_OPTS="-Dorg.gradle.daemon=false"

COPY build.gradle.kts settings.gradle.kts ./
COPY auth-service/build.gradle.kts ./auth-service/
COPY gateway-service/build.gradle.kts ./gateway-service/

RUN gradle dependencies --no-daemon || true

COPY auth-service/src ./auth-service/src
COPY gateway-service/src ./gateway-service/src

ARG SERVICE_NAME

RUN gradle :${SERVICE_NAME}:bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

ARG SERVICE_NAME

COPY --from=build /app/${SERVICE_NAME}/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
