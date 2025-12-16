# Use Eclipse Temurin JRE 17 as the base image (smaller than JDK)
FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Copy the fat JAR from the build
COPY build/libs/dragoncon-reminder-bot.jar /app/app.jar

# Create a non-root user to run the application
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Change ownership of the app directory
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose any ports if needed (Discord bots typically don't need exposed ports)
# EXPOSE 8080

# Set environment variables (these should be overridden at runtime)
ENV DISCORD_TOKEN="" \
    DISCORD_CHANNEL_ID=""

# Health check (optional - checks if the process is running)
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD pgrep -f "java.*app.jar" || exit 1

# Run the application
CMD ["java", "-jar", "/app/app.jar"]
