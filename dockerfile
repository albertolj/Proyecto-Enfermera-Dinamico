# Fase 1: Construcción del proyecto usando Maven
FROM maven:3.9.4-eclipse-temurin-17 AS build

# Configurar el directorio de trabajo
WORKDIR /app

# Copiar el archivo de configuración del proyecto (pom.xml) y descargar dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuente al contenedor
COPY src ./src

# Construir el proyecto y empaquetarlo
RUN mvn clean package -DskipTests

# Fase 2: Imagen final para ejecutar la aplicación
FROM openjdk:17-jdk-slim

# Configurar el directorio de trabajo
WORKDIR /app

# Copiar el archivo JAR generado en la fase de construcción
COPY --from=build /app/target/cc-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto que usa la aplicación
EXPOSE 8081

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
