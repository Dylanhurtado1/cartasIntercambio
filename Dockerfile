# Usa una imagen base de Java
FROM openjdk:17-jdk-slim

# Especifica el directorio de trabajo en el contenedor
WORKDIR /app

# Copia el archivo JAR construido en tu proyecto al contenedor
COPY target/cartasIntercambio-0.0.1-SNAPSHOT.jar app.jar

# Expone el puerto que usa tu aplicación
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]