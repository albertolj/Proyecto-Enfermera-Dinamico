# Usar una imagen base de OpenJDK
FROM openjdk:17-jdk-slim

# Configurar el directorio de trabajo
WORKDIR /app

# Copiar el archivo JAR de la carpeta `target` al contenedor
COPY target/cc-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto que usa la aplicación
EXPOSE 8081

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
