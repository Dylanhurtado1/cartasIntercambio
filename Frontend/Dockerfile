# Usa una imagen base oficial de Node.js
FROM node:23-slim

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia los archivos de dependencias
COPY package*.json ./

# Instala las dependencias
RUN npm install

# Copia el resto de los archivos de la aplicación
COPY . .

# Expone el puerto en el que corre tu servidor (cambia 3000 si usas otro)
EXPOSE 9090

# Comando para correr tu aplicación
CMD ["node", "app.js"]
