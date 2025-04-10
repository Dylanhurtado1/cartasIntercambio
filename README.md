# Cartas Intercambio

## Descripción
Esta es una aplicación para intercambiar cartas de juegos coleccionables como Magic: The Gathering, Pokémon y Yu-Gi-Oh!. Los usuarios pueden publicar cartas de su colección para intercambio o venta y buscar cartas disponibles de otros usuarios.

## Funcionalidades Implementadas

### Endpoints

1. **Crear Carta**
    - **Método**: `POST`
    - **URL**: `/cartas`
    - **Descripción**: Permite a los usuarios publicar una carta en su colección.
    - **Cuerpo de la Solicitud (JSON)**:
      ```json
      {
          "juego": "Magic: The Gathering",
          "nombre": "Black Lotus",
          "estado": "Excelente",
          "imagenes": ["https://ejemplo.com/black-lotus.jpg"],
          "valorEstimado": 5000.00,
          "cartasInteres": ["Ancestral Recall", "Time Walk"]
      }
      ```

2. **Listar Cartas**
    - **Método**: `GET`
    - **URL**: `/cartas`
    - **Descripción**: Permite a los usuarios buscar y listar las cartas disponibles.

## Estructura del Proyecto

La estructura del proyecto es la siguiente:

- **Dockerfile**: Define cómo construir la imagen de la aplicación.
- **docker-compose.yml**: Contiene la configuración de los servicios a ejecutar en contenedores, como la aplicación en sí.

## Cómo Ejecutar la Aplicación

### Prerequisitos

- Java 17
- Maven
- Docker y Docker Compose

### Instrucciones para Ejecutar

1. Clona el repositorio en tu máquina local:
   ```bash
   git clone https://github.com/Dylanhurtado1/cartasIntercambio.git

Navega al directorio del proyecto:

```bash cd cartasIntercambio```

Compila el proyecto:

```mvn clean package```

Construye y ejecuta el contenedor:

```docker-compose up```

Accede a la API:

La API estará disponible en http://localhost:8080.

