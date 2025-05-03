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
        "cartaOfrecida":
                {
                    "juego": "Magic",
                    "nombre": "Blastoise",
                    "estado": "BUENO",
                    "imagenes": [
                        "http://ejemplo.com/cartaA_imagen1.jpg",
                        "http://ejemplo.com/cartaA_imagen2.jpg"
                    ]
                },
            "precio": 10000,
            "cartasInteres": [
                {
                    "nombre": "Mago oscuro",
                    "juego": "Yugioh",
                    "estado": "BUENO",
                    "imagenes": [
                        "http://ejemplo.com/cartaC_imagen1.jpg"
                    ]
                }
            ],
        "publicador": {
            "id": 1,
            "user": "wololo",
            "nombre": "Juan",
            "apellido": "Perez",
            "email": "juan.perez@ejemplo.com",
            "fechaNacimiento": "2025-04-13T10:00:00Z"
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
      "monto": 120.00,
      "cartasOfrecidas": [
        {
            "juego": "Juego de Cartas A",
            "nombre": "Carta Rara 1",
            "estado": "NUEVO",
            "imagenes": [
            "http://example.com/imagenes/carta1_1.jpg",
            "http://example.com/imagenes/carta1_2.jpg"
            ]
        },
        {
          "juego": "Juego de Cartas B",
          "nombre": "Carta Rara 2",
          "estado": "BUENO",
          "imagenes": [
            "http://example.com/imagenes/carta2_1.jpg"
          ]
        }
      ],
      "ofertante": {
        "id": 1,
        "user": "ofertante123",
        "nombre": "Juan",
        "apellido": "Pérez",
        "email": "juan.perez@example.com",
        "fechaNacimiento": "1990-05-20T00:00:00Z"
      }
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
   - **Método**: `PATCH`
   - **URLs**: `publicaciones/ofertas/{idOferta}`     
   - **Descripción**: Permite aceptar o rechazar una oferta.
   - **Header de la solicitud (JSON)**: `Content-Type: "application/json-patch+json"`
   - **Cuerpo de la Solicitud (JSON)**:
      ```json
      [{
        "op":"replace",
        "path":"/estado",
        "value":"ACEPTADO"
        }]
 
 8. **Buscar estadisticas sobre el uso de la aplicacion**
    - **Método**: `GET`
    - **URLs**: `/estadisticas`     
    - **Descripción**: Permite obtener estadisticas sobre las publicaciones de la plataforma.

## Gestión de Usuarios 

1. **Registrar usuario**
    - **Método**: `POST`
    - **URL**: `/usuarios`
    - **Descripción**: Permite crear un nuevo usuario.
      - **Cuerpo de la Solicitud (JSON)**:
         ```json
         {
          "user": "juampi",
          "nombre": "Juan Pablo",
          "correo": "juanpablo.alumno@cartas.com",
          "password": "Juampi!2024"
         }

2. **Registrar administrador**
    - **Método**: `POST`
    - **URL**: `/usuarios/admins`
    - **Descripción**: Permite crear un nuevo usuario con rol de administrador.
        - **Cuerpo de la Solicitud (JSON)**:
           ```json
          {
           "user": "admin2",
           "nombre": "Florencia",
           "correo": "florencia.admin@cartas.com",
           "password": "adminSegura2024",
          }

3. Listar usuarios
    - **Método**: `GET`
    - **URL**: `/usuarios`
    - **Descripción**: Devuelve la lista de todos los usuarios registrados.
    ```json
   [
      {
       "user": "juampi",
       "nombre": "Juan Pablo",
       "correo": "juanpablo.alumno@cartas.com"
     },
     {
       "user": "martina99",
       "nombre": "Martina",
       "correo": "marti99@email.com"
     }
   ]

4. Buscar usuario por ID
   - **Método**: `GET`
   - **URL**: `/usuarios/{id}`
   - **Descripción**: Devuelve el usuario con el ID solicitado.
   ```json
   {
    "user": "juampi",
    "nombre": "Juan Pablo",
    "correo": "juanpablo.alumno@cartas.com"
    }

5. Actualizar usuario

- **Método**: `PUT`
- **URL**: `/usuarios/{id}`
- **Descripción**: Actualiza los datos de un usuario.
  ```json
  {
  "user": "nuevo_user",
  "nombre": "Nombre Nuevo",
  "correo": "nuevo@email.com",
  "password": "passActualizada"
  }

Respuesta:

    
    {
     "user": "nuevo_user",
     "nombre": "Nombre Nuevo",
     "correo": "nuevo@email.com"
    }


5. Borrar usuario
- **Método**: `DELETE`
- **URL**: `/usuarios/{id}`
- **Descripción**: Elimina el usuario con el ID dado.
- **Respuesta: 204 No Content**

6. Buscar usuarios por filtro
- **Método**: `DELETE`
- **URL**: `/usuarios/search?nombre=Juan`
- **Descripción**: Devuelve la lista de usuarios que coinciden con los parámetros enviados.

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
3. Navega a la carpetaBackend

   ```cd Backend```

4. Compila el proyecto:

    ```mvn clean package```

5. Construye y ejecuta el contenedor:

Si hiciste cambios en el código, SIEMPRE usá

```docker compose up --build```

para asegurarte de que tu código actualizado esté corriendo en el contenedor Docker.

Despúes de levantarlo la primera vez, si no tocás el código, podés usar solo

```docker compose up```

### Accede a la API:

El front estará disponible en http://localhost:9090.


