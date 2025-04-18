# Cartas Intercambio

## Descripción
Esta es una aplicación para intercambiar cartas de juegos coleccionables como Magic: The Gathering, Pokémon y Yu-Gi-Oh!. Los usuarios pueden publicar cartas de su colección para intercambio o venta y buscar cartas disponibles de otros usuarios.

## Funcionalidades Implementadas

### Endpoints

*Aclaración: al no tener implementado una BBDD, en esta instancia se pasan los usuarios completos en el cuerpo de la solicitud*

1. **Crear una publicación**
    - **Método**: `POST`
    - **URL**: `/publicaciones`
    - **Descripción**: Permite a los usuarios realizar la publicación de una carta.
    - **Cuerpo de la Solicitud (JSON)**:
      ```json
      {
        "fecha": "2025-04-13T10:00:00Z",
        "descripcion": "Publicación de prueba para intercambio de cartas",
        "demanda": {
            "cartasOfrecidas": [
                {
                    "nombre": "Blastoise",
                    "juego": "Magic",
                    "estado": "Maso",
                    "imagenes": [
                        "http://ejemplo.com/cartaA_imagen1.jpg",
                        "http://ejemplo.com/cartaA_imagen2.jpg"
                    ]
                }
            ],
            "descripcion": "Estoy buscando cartas épicas a cambio de cartas raras.",
            "precio": 10000,
            "cartasInteres": [
                {
                    "nombre": "Mago oscuro",
                    "juego": "Yu-gi-oh!",
                    "estado": "Nuevo",
                    "imagenes": [
                        "http://ejemplo.com/cartaC_imagen1.jpg"
                    ]
                }
            ]
        },
        "publicador": {
            "id": 1,
            "user": "wololo",
            "nombre": "Juan",
            "apellido": "Perez",
            "email": "juan.perez@ejemplo.com",
            "fechaNacimiento": "1850-04-13"
        },
        "estado": "PENDIENTE"
      }
2. **Listar publicaciones**
    - **Método**: `GET`
    - **URL**: `/publicaciones`
    - **Descripción**: Permite obtener los datos de todas las publicaciones, incluyendo sus fechas de emisión y ofertas correspondientes.
 3. **Filtrar publicaciones**
    - **Método**: `GET`
    - **URLs**: 
        - `/publicaciones/nombre/{nombre}`
        - `/publicaciones/juego/{juego}`
        - `/publicaciones/estado/{estado}`
        - `/publicaciones/precio/{precio}`     
    - **Descripción**: Permite filtrar las publicaciones por nombre, juego, estado ó el precio de la carta.
 4. **Crear una oferta**
    - **Método**: `POST`
    - **URLs**: `/publicaciones/{idPublicacion}/ofertas`     
    - **Descripción**: Permite realizar una oferta sobre una publicación.
    - **Cuerpo de la Solicitud (JSON)**:
      ```json
      {
        "fecha": "2025-04-13T10:00:00Z",
        "publicacion": {
            "id": 123,
            "titulo": "Oferta Especial de Cartas",
            "descripcion": "Oferta en cartas raras de colección",
            "precio": 150.00,
            "fechaPublicacion": "2025-04-01T09:00:00Z"
        },
        "monto": 120.00,
        "cartasOfrecidas": [
        {
          "nombre": "Carta Rara 1",
          "juego": "Juego de Cartas A",
          "estado": "Nuevo",
          "imagenes": [
            "http://example.com/imagenes/carta1_1.jpg",
            "http://example.com/imagenes/carta1_2.jpg"
          ]
        },
        {
          "nombre": "Carta Rara 2",
          "juego": "Juego de Cartas B",
          "estado": "Usado",
          "imagenes": [
            "http://example.com/imagenes/carta2_1.jpg"
          ]
        }
        ],
        "ofertante": {
        "id": 789,
        "user": "ofertante123",
        "nombre": "Juan",
        "apellido": "Pérez",
        "email": "juan.perez@example.com",
        "fechaNacimiento": "1990-05-20T00:00:00Z"
        },
        "estado": "Pendiente"
      }
 5. **Buscar publicaciones por usuario**
    - **Método**: `GET`
    - **URLs**: `publicaciones/usuarios/{idUsuario}`     
    - **Descripción**: Permite filtrar las publicaciones por ID de usuario.

 6. **Buscar ofertas para una publicacion de un usuario**
    - **Método**: `GET`
    - **URLs**: `publicaciones/{idPublicacion}/ofertas/{idUsuario}`     
    - **Descripción**: Permite obtener las ofertas realizadas por un usuario.
   
7. **Responder a una oferta de una publicacion de un usuario**
   - **Método**: `PUT`
   - **URLs**: `publicaciones/{idPublicacion}/ofertas/{idUsuario}/{idOferta}`     
   - **Descripción**: Permite aceptar o rechazar una oferta.
   - **Cuerpo de la Solicitud (JSON)**:
      ```json
      Respuesta
 
 8. **Buscar estadisticas sobre cantidad de publicaciones**
    - **Método**: `GET`
    - **URLs**: `publicaciones/estadisticas/publicaciones`     
    - **Descripción**: Permite obtener la cantidad de publicaciones creadas por los usuarios.
   
 9. **Buscar estadisticas sobre cantidad de publicaciones por juego**
    - **Método**: `GET`
    - **URLs**: `publicaciones/estadisticas/juegos`     
    - **Descripción**: Permite obtener la cantidad de publicaciones creadas por los usuarios para cada juego.
      
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

2. Navega al directorio del proyecto:

    ```bash cd cartasIntercambio```

3. Compila el proyecto:

    ```mvn clean package```

4. Construye y ejecuta el contenedor:

    ```docker-compose up```

### Accede a la API:

La API estará disponible en http://localhost:8080.


