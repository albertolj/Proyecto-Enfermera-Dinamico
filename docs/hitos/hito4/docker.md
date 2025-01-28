# Documentación del Proyecto de Clúster de Contenedores

## Estructura del Clúster de Contenedores

### Justificación de la Estructura
La estructura del clúster de contenedores está diseñada para proporcionar una solución completa y escalable para la aplicación. El clúster incluye los siguientes servicios:

- **app**: Contenedor principal que ejecuta la aplicación Java.
- **db1**: Contenedor de PostgreSQL para la base de datos de la aplicación.
- **phpmyadmin**: Interfaz web para gestionar la base de datos.
- **nginx**: Proxy inverso y gestor de certificados SSL.
- **db**: Contenedor de MariaDB para la base de datos de Nginx Proxy Manager.

La red `net` conecta todos los contenedores, permitiendo la comunicación interna entre ellos.

## Configuración de los Contenedores

### app
- **Base**: `openjdk:17-jdk-slim`
- **Justificación**: Utiliza una imagen ligera de OpenJDK 17 para ejecutar la aplicación Java.
- **Configuración**:
  - Construcción desde el Dockerfile.
  - Depende de `db1` y `nginx`.
  - Variables de entorno para la configuración de la base de datos.

### db1
- **Base**: `postgres:15`
- **Justificación**: PostgreSQL es una base de datos con buena conectividad con Spring Boot.
- **Configuración**:
  - Variables de entorno para el usuario, contraseña y base de datos.
  - Volumen para persistencia de datos.

### phpmyadmin
- **Base**: `phpmyadmin:5.2.1`
- **Justificación**: Proporciona una interfaz web para gestionar la base de datos.
- **Configuración**:
  - Variable de entorno `PMA_ARBITRARY` para permitir conexiones arbitrarias.
  - Política de reinicio en caso de fallo.

### nginx
- **Base**: `jc21/nginx-proxy-manager:2.12.2`
- **Justificación**: Nginx Proxy Manager facilita la gestión de proxies inversos y certificados SSL.
- **Configuración**:
  - Puertos expuestos para HTTP, HTTPS y administración.
  - Volúmenes para persistencia de datos y certificados.
  - Variables de entorno para la configuración de la base de datos.
  - Política de reinicio en caso de fallo.

### db
- **Base**: `jc21/mariadb-aria:10.11.5`
- **Justificación**: Base de Datos para almacenar los datos de Nginx.
- **Configuración**:
  - Variables de entorno para el usuario, contraseña y base de datos.
  - Volumen para persistencia de datos.
  - Política de reinicio en caso de fallo.

## Dockerfile del Contenedor de la Aplicación

El Dockerfile utiliza una imagen base de OpenJDK 17 slim para minimizar el tamaño del contenedor. Se copia el archivo JAR generado por Maven y se expone el puerto 8081 para la aplicación.

## Publicación del Conetenedor en GitHub Packages

El archivo compose.yml define la configuración de todos los servicios del clúster, incluyendo las dependencias, variables de entorno, volúmenes y políticas de reinicio. El paquete se publica en el perfil y no en el propio repositorio debido a un problema al tener el repositorio nombres en mayúsculas.

El paquete se encuentra en esta [url](https://github.com/albertolj?tab=packages).