
FROM openjdk:21-jdk-slim

WORKDIR /app

COPY ./backend-0.0.1-SNAPSHOT.jar app.jar
COPY wait-for-it.sh /usr/local/bin/wait-for-it
RUN chmod +x /usr/local/bin/wait-for-it

EXPOSE 8443

CMD ["java", "-jar", "app.jar"]
