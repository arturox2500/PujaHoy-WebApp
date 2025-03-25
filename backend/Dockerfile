FROM maven:3.9.4-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src


RUN mvn clean package -DskipTests


FROM openjdk:21-jdk-slim

WORKDIR /app

COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto de la aplicación
EXPOSE 8443

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "app.jar"]