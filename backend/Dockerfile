# Etapa 1: Construcción del frontend Angular
FROM node:20 AS frontend-build
WORKDIR /app

# Copiamos el package.json y el package-lock.json para instalar dependencias
COPY frontend/package*.json ./
RUN npm install

# Copiamos el código fuente de Angular
COPY frontend/ .

# Construimos el proyecto de Angular para producción
RUN npm run build --prod

# Etapa 2: Construcción del backend con Maven
FROM maven:3.9.4-eclipse-temurin-21 AS backend-build
WORKDIR /app

# Copiamos el archivo pom.xml y el código fuente de Spring Boot
COPY backend/pom.xml .
COPY backend/src ./src

# Copiamos el build de Angular en el directorio static de Spring Boot
COPY --from=frontend-build /frontend/dist/tu-nombre-app/ ./src/main/resources/static/

# Compilamos el backend con Maven
RUN mvn clean package -DskipTests

# Etapa 3: Imagen final con el backend listo para producción
FROM openjdk:21-jdk-slim
WORKDIR /app

# Copiamos el JAR generado por Maven desde la etapa anterior
COPY --from=backend-build /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

# Exponemos el puerto de la aplicación
EXPOSE 8443

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "app.jar"]
