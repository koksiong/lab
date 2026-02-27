# --- Stage 1: Build ---
# Use a full Maven image to compile the application
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# 1. Copy only the pom.xml first to cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 2. Copy the source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# --- Stage 2: Runtime ---
# Use a lightweight JRE Alpine image for the final container
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 3. Security: Create a non-root user to run the app
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# 4. Copy ONLY the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# 5. Run the application
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]