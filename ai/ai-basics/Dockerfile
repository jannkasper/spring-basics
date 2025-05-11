FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the Maven POM file and download dependencies
COPY pom.xml .
COPY src ./src
COPY frontend ./frontend

# Build the application
RUN mvn clean package -DskipTests

# Runtime image
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Set environment variables
ENV SPRING_AI_OPENAI_API_KEY=""

# Set the entrypoint
ENTRYPOINT ["java", "-jar", "app.jar"]

# Default port exposure
EXPOSE 8080 