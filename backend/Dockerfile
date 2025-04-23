# Stage 1: Angular build
FROM node:20 AS frontend-build
WORKDIR /app

COPY frontend/package*.json ./
RUN npm install

COPY frontend/ .
RUN npm install -g @angular/cli@17.3.14
RUN ng build --configuration production --base-href="/new/"

# Stage 2: Backend build
FROM maven:3.9.4-eclipse-temurin-21 AS backend-build
WORKDIR /app


COPY backend/pom.xml .
COPY backend/src ./src

COPY --from=frontend-build /app/dist/pujahoy/browser ./src/main/resources/static/new

RUN mvn clean package -DskipTests

# Stage 3: Final image
FROM openjdk:21-jdk-slim
WORKDIR /app

COPY --from=backend-build /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8443

CMD ["java", "-jar", "app.jar"]
