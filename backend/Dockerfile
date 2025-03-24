
FROM openjdk:21-jdk-slim

WORKDIR /app

COPY ./backend-0.0.1-SNAPSHOT.jar app.jar
RUN chmod +x /usr/local/bin/wait-for-it

EXPOSE 8443

CMD ["java", "-jar", "app.jar"]
