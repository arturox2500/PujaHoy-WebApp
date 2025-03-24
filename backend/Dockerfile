
FROM openjdk:21-jdk-slim

WORKDIR /app

COPY ./backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8443

CMD ["java", "-jar", "app.jar"]
