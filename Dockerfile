FROM gradle:8-jdk21 AS build
WORKDIR /app

# Copy build files for cache dependence
COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle ./gradle
RUN chmod +x gradlew

RUN ./gradlew dependencies --no-daemon

COPY src ./src

# Build jar-file (skip tests for high speed)
RUN ./gradlew bootJar -x test --no-daemon

# Run project
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy builded jar-file an build/libs
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]