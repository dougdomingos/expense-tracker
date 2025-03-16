FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy pre-built JAR file into container
COPY target/expensetracker*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod",  "app.jar"]