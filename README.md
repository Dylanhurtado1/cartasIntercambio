# Cartas Intercambio

## Descripción
Esta es una aplicación para intercambiar cartas de juegos coleccionables como Magic: The Gathering, Pokémon y Yu-Gi-Oh!. Los usuarios pueden publicar cartas de su colección para intercambio o venta y buscar cartas disponibles de otros usuarios.

## Funcionalidades Implementadas

### Endpoints

*Aclaraciones:*

*Al no tener implementado una BBDD, en esta instancia se pasan los usuarios completos en el cuerpo de la solicitud. Tanto cuando se haga una publicación o una oferta, serán con usuarios hardcodeados.*

*Además, aún no está implementado el sistema de carga de imágenes. Por ahora, solo guarda el nombre de las imágenes*

| **Enlace**                                                        | **Descripción**   |
|-------------------------------------------------------------------|-----|
| [localhost:9090](http://localhost:9090)                           | En este enlace se enlistan todas las publicaciones del sistema. Incluye un buscador para realizar filtros. Al hacer clic en una publicación, se accede a sus detalles. |
| [localhost:9090/publicacion/id](http://localhost:9090/publicacion/id) | Página donde se muestra en detalle una publicación. Abajo de todo se puede realizar una oferta si la publicación sigue activa. Si no, el formulario de oferta no estará disponible. |
| [localhost:9090/publicacion/id/ofertas](http://localhost:9090/publicacion/id/ofertas) | Muestra las ofertas recibidas por la publicación. Cada oferta puede ser aceptada o rechazada. Si una oferta es aceptada, las demás se rechazan automáticamente y la publicación queda finalizada. |
| [localhost:9090/publicar](http://localhost:9090/publicar)     | Formulario para subir nuevas publicaciones al sistema.                                                                                                      |

## Estructura del Proyecto

La estructura del proyecto es la siguiente:

- **Backend**: Es nuestro servicio backend, en dónde está la lógica del negocio. Está hecho en Java + Springboot.
- **Frontend**: Es la UI del proyecto, en el cuál el usuario podrá interactuar con nuestro sistema. Está hecho con Nodejs + expressjs y de front con Vue3 
- **Dockerfile**: Define cómo construir la imagen de cada aplicación en el sistema. Tanto backend como frontend tienen sus propios dockerfiles respectivamente.
- **docker-compose.yml**: Contiene la configuración de los servicios a ejecutar en contenedores.

## Cómo Ejecutar la Aplicación

### Pre requisitos

- Nodejs
- Java 17
- Maven
- Docker y Docker Compose

### Instrucciones para Ejecutar

1. Cloná el repositorio en tu máquina local:
   ```bash
   git clone https://github.com/Dylanhurtado1/cartasIntercambio.git


> **IMPORTANTE:**  
> _Nunca subas el archivo `.env` con tus secrets a GitHub.  
> Usá el template `.env.example`

** Configurá los secretos de AWS para imágenes**

Copiá el `.env.example` como `.env` y editá los valores según tu S3 real:

```bash
cp .env.example .env
``` 

o creá uno nuevo, así:

```
AWS_S3_BUCKET=nombre-de-tu-bucket-s3
AWS_ACCESS_KEY_ID=CAMBIA-ESTO
AWS_SECRET_ACCESS_KEY=CAMBIA-ESTO
AWS_S3_REGION=us-east-1
```



2. Navegá al directorio del proyecto:

    ```bash cd Backend```

3. Compilá el proyecto:

    ```mvn clean package -DskipTests```

4. Volvé a la raíz del proyecto

    ```cd /..```

5. Construí y ejecutá los contenedores:

    **Si hiciste cambios en el código, SIEMPRE usá:**
    
    ```docker compose up --build```
    
   *Esto es para asegurarte de que tu código actualizado esté corriendo en el contenedor Docker.*
    
   **Despúes de levantarlo la primera vez, si no tocás el código, podés usar:**
    
    ```docker compose up```

### 📋 Acceso rápido a la base de datos por navegador

- Podés entrar a [http://localhost:8081](http://localhost:8081) y ver/modificar los datos de MongoDB usando el cliente web **mongo-express**.
- USERNAME = admin
- PAASWORD = admin123
    
    ### Para acceder a la API:
    
    El front estará disponible en http://localhost:9090.


