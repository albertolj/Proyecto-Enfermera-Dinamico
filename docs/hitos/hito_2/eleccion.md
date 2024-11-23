## 1. Elección y configuración del gestor de tareas

En el proyecto se ha utilizado **Maven** como gestor de tareas y construcción. Maven es una herramienta utilizada en proyectos Java para gestionar dependencias, construir el proyecto y ejecutar las pruebas.

### Justificación:
- **Maven** permite la integración sencilla con herramientas como Jenkins o GitHub Actions para automatizar las tareas de construcción y ejecución de pruebas. 
- En este proyecto, se utiliza el comando `mvn clean install` para limpiar y compilar el proyecto, y `mvn test` para ejecutar las pruebas unitarias.
- La configuración de Maven está definida en el archivo `pom.xml`, que incluye las dependencias necesarias para Spring Boot, PostgreSQL y otras bibliotecas utilizadas en el proyecto.

## 2. Elección y uso de la biblioteca de aserciones (1.5 puntos)

En lugar de utilizar bibliotecas externas como AssertJ, el proyecto emplea las herramientas de **Spring Test** para realizar las verificaciones en las pruebas de integración de los controladores.

### Justificación:
- Se utiliza la clase `MockMvcResultMatchers` de Spring para realizar las aserciones sobre las respuestas HTTP de los endpoints. Esto incluye verificar el código de estado de la respuesta, el contenido del cuerpo y la estructura de los datos JSON.
- Por ejemplo:
  - `andExpect(status().isOk())`: Verifica que la respuesta sea un estado HTTP 200.
  - `andExpect(jsonPath("$.size()").value(2))`: Verifica que el tamaño del arreglo en la respuesta sea 2.
  - `andExpect(content().string("Medico no encontrado"))`: Verifica que el mensaje de error en el cuerpo de la respuesta sea el esperado.

## 3. Elección y uso del marco de pruebas

El proyecto utiliza **Spring Boot Test** como marco de pruebas, específicamente con el componente `MockMvc` para probar los controladores web.

### Justificación:
- **Spring Boot Test** se utiliza para realizar pruebas de integración de los controladores de la aplicación sin necesidad de arrancar todo el contexto de Spring o la base de datos real.
- Por ejemplo, en las pruebas de la clase `MedicoControllerTest`, se utiliza la anotación `@WebMvcTest(MedicoController.class)` para cargar solo los componentes necesarios para probar los controladores, como el controlador `MedicoController` y los servicios necesarios mockeados con `@MockBean`.
- Se emplea el componente `MockMvc` para simular peticiones HTTP a los endpoints y verificar las respuestas esperadas.
- Se pueden realizar las pruebas sin tener la base de datos funcionando.

## 4. Comparación con otras tecnologías

Para las pruebas de integración unitarias, se ha utilizado **Spring Boot Test** con **MockMvc** respecto de otras tecnologías, como por ejemplo **TestNG**.
TestNG tiene algunas características mejores de lo utilizado como: ser un framework de pruebas más general, disponer de una configuración manual de los Mocks, un enfoque más orientado a las pruebas unitarias y de disponer de más características avanzadas. Sin embargo, se ha utilizado Spring Boot Test con MockMvc debido a su integración directa con el framework y por haberlo utilizado anteriormente en otros proyectos.