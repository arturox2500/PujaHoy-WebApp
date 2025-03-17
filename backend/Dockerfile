
FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/pujahoy.jar app.jar

EXPOSE 8443

CMD ["java", "-jar", "app.jar"]
