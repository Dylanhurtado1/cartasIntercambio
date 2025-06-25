# Cartas Intercambio

## Descripción
Esta es una aplicación para intercambiar cartas de juegos coleccionables como Magic: The Gathering, Pokémon y Yu-Gi-Oh!. Los usuarios pueden publicar cartas de su colección para intercambio o venta y buscar cartas disponibles de otros usuarios.

## Funcionalidades Implementadas

### Endpoints

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

## Cómo Ejecutar la Aplicación en Modo Local

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
JWT_SECRET=aca-va-clave-secreta-jwt
```



2. Navegá al directorio del proyecto:

    ```bash cd Backend```

3. Compilá el proyecto:

    ```mvn clean package```

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
- PASSWORD = admin123
    
    ### Para acceder a la API:
    
    El front estará disponible en http://localhost:9090.

## Como realizar el deploy en AWS EC2 con Docker

### Pre requisitos
- Tener una cuenta en AWS

### Instrucciones para deployar en EC2

1. Lanzar una instancia EC2 en la región us-east-1
   - Tipo de instancia t2.micro
   - Sistema Ubuntu 24.04 de 64 bits
   - Crear un nuevo par de claves
   - Configurar el Security Group para que acepte puertos 8080 y 9090 desde cualquier lugar
   - Configurar almacenamiento: un disco EBS con 16 GB
   - Descargar el archivo .pem (clave privada que te permite conectarte a la instancia mediante SSH)

2. Abrir una consola y acceder por SSH
   ```
   chmod 400 nombre-archivo-key.pem #SOLO EJECUTARLO LA PRIMERA VEZ
   ```
   ```
   ssh -i "ruta/nombre-archivo-key.pem" ubuntu@<IP_PUBLICA_EC2>
   ```

3. Actualizar paquetes
   ```
   sudo apt update && sudo apt upgrade -y
   ```

4. Instalar Docker y Docker Compose
   - Instalar Docker
     ```
     sudo apt install docker.io -y
     sudo systemctl start docker
     sudo systemctl enable docker 
     sudo usermod -aG docker ubuntu
     ```
   - Cerrar la sesión y volver a entrar para que se apliquen los cambios
     ```
     exit
     ```
     ```
     ssh -i "ruta/nombre-archivo-key.pem" ubuntu@<IP_PUBLICA_EC2>
     ```
   - Instalar Docker Compose
     ```
     sudo curl -L "https://github.com/docker/compose/releases/download/v2.23.3/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
     sudo chmod +x /usr/local/bin/docker-compose
     ```
   - Chequear que se haya instalado correctamente
     ```
     docker-compose --version
     ```

5. Instalar y configurar Java 17
   - Instalar java 17 
     ```
     sudo apt update
     sudo apt install openjdk-17-jdk -y
     ```
   - Setear la versión de Java
     ```
     sudo update-alternatives --config java
     ```
     Configurar las variables de entorno
     ```
     export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
     export PATH=$JAVA_HOME/bin:$PATH
     source ~/.bashrc
     ```
   - Verificar que se haya seteado la versión de Java correctamente 
     ```
     echo $JAVA_HOME
     java -version
     ```

6. Instalar Maven
   ```
   sudo apt install maven -y
   ```
   Chequeamos que se haya instalado correctamente:
   ```
   mvn --version
   ```

7. Clonar el repositorio
   ```
   git clone https://github.com/Dylanhurtado1/cartasIntercambio.git
   ```
   
   ```
   cd cartasIntercambio
   ```

8. Configurar el .env
   ```
   nano .env
   ```
   Cargar las claves de S3:

   ```
    AWS_S3_BUCKET=nombre-de-tu-bucket-s3
    AWS_ACCESS_KEY_ID=CAMBIA-ESTO
    AWS_SECRET_ACCESS_KEY=CAMBIA-ESTO
    AWS_S3_REGION=us-east-1
    JWT_SECRET=aca-va-clave-secreta-jwt
   ```
   
   Ctrl+O y Enter para guardar

   Ctrl+X para salir


9. Compilar y levantar la app
   - Navegar al directorio del proyecto
   ```
   cd Backend
   ```
   - Compilar
   ```
   mvn clean package
   ```
   - Volver a la raíz del proyecto
   ```
   cd ..
   ```
   - Construir y ejecutar los contenedores
   ```
   docker-compose up -d --build
   ```

10. Para detener la instancia EC2
   - Primero detener la ejecución de los contenedores
   ```
   docker-compose down
   ```
   - Cerramos la sesión
   ```
   exit
   ```
   - Desde la página de AWS, detenemos la instancia EC2


📌 **NOTA**: La instalación de Docker, Maven y Java, así como la configuración del archivo .env, solo es necesaria la primera vez que se prepara la instancia.


### Para acceder desde el navegador:

Podés ver los datos de MongoDB usando el cliente web **mongo-express** desde **https://mongo.intercambiocartas.online/**

La aplicación estará disponible en:
- **https://intercambiocartas.online/**

- **https://www.intercambiocartas.online/**

## Tests
La aplicación incluye tests unitarios para los servicios y controladores. Utilizamos Jacoco para medir el coverage. 

### Cómo generar el reporte de cobertura

Desde el directorio del proyecto:
```bash
cd Backend
mvn clean test
mvn jacoco:report
```
El reporte HTML se genera en target/site/jacoco/index.html

## Seguridad

Implementamos múltiples capas de protección para asegurar la estabilidad de la aplicación frente a posibles ataques, como DDoS o tests de carga:

- Dominio propio **intercambiocartas.online**

- **Cloudflare como proxy DNS**:
  * Protección contra DDoS y el modo Bot Fight.
  * Se agregó una regla personalizada que bloquea el acceso a cualquier IP fuera de Argentina.
  * Opera como proxy para ocultar la IP real de la instancia EC2.

- **NGINX instalado en la instancia EC2**:
  * Proxy inverso entre el tráfico externo y los contenedores de la aplicación.
  * Configuración para limitar peticiones a 60 por minuto por IP (retorna 429 Too Many Requests).

- **HTTPS**:
  * Se instaló Certbot en la instancia para obtener un certificado SSL gratuito de Let's Encrypt.
  * Se configuró NGINX para forzar HTTPS y asegurar el tráfico.

- **Frontend**:

  Bloquea durante 1 minuto si se realizan más de 75 requests en 1 minuto.

- **Backend**:
  * Clase RateLimitFilter que bloquea por 1 minuto a cualquier IP que supere 50 peticiones por minuto.
  * El filtro devuelve un 429 Too Many Requests cuando se excede el límite.

