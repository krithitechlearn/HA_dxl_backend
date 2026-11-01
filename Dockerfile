# Multi-stage Dockerfile for HA DXL Backend
# Stage 1: Build
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY Backend/pom.xml .
COPY Backend/src ./src
RUN apk add --no-cache maven && mvn -f pom.xml dependency:go-offline -B
RUN mvn -f pom.xml package -DskipTests -B

# Stage 2: Runtime (non-root user)
FROM eclipse-temurin:21-jre-alpine AS runtime
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
RUN chown appuser:appgroup app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Djava.security.egd=file:/dev/./urandom", "app.jar"]
