# Stage 1: Build the application
FROM maven:3.9.5-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
# Download dependencies (cache layer)
RUN mvn dependency:go-offline -B
COPY src ./src
# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jre-jammy

# Create a non-root user for security
RUN addgroup --system spring && adduser --system --group spring
USER spring:spring

WORKDIR /app
COPY --from=build --chown=spring:spring /app/target/*.jar app.jar

EXPOSE 8080

# Environment variable to tune Java memory for the 1GB RAM server
ENV JAVA_OPTS="-Xmx300m -Xms150m -XX:+UseSerialGC"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]