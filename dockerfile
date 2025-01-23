# Fase única: Utilizar una imagen preconstruida para pruebas
FROM kimb88/hello-world-spring-boot:latest

# Configurar el directorio de trabajo (opcional, según la imagen base)
WORKDIR /app

# Exponer el puerto utilizado por la aplicación (Render requiere esta declaración)
EXPOSE 8080

# Configurar el comando de inicio para que utilice la variable de entorno PORT
ENTRYPOINT ["java", "-Dserver.port=${PORT}", "-jar", "app.jar"]