# Multi-stage Docker build for CineBook Web
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy Maven POM and project files
COPY backend/pom.xml .
COPY backend/src ./src

# Package application jar (skipping unit tests during container assembly)
RUN mvn clean package -DskipTests

# Production Runtime Stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Create unprivileged runtime user
RUN addgroup --system cinegroup && adduser --system --ingroup cinegroup cineuser

# Copy executable jar from builder stage
COPY --from=build /app/target/*.jar app.jar

USER cineuser
EXPOSE 8080

# Run Spring Boot application
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]

