FROM maven:3.8-openjdk-11

WORKDIR /app

# Copy the configuration files
COPY config*.yml ./

# Create directory for logs
RUN mkdir -p /app/logs

# Expose ports
EXPOSE 8080 8081

# Default command executes the JAR with the local configuration
CMD ["java", "-jar", "target/mydos-backend-service-1.0-SNAPSHOT.jar", "server", "config-local.yml"]