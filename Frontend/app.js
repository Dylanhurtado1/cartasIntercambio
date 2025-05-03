const path = require('path')
const port = 9090
const express = require("express")
const app = express();
const pathHTML = __dirname + '/views'


app.use('/public', express.static(path.join(__dirname, 'public'))); //tener acceso a los archivos estáticos

app.listen(port, () => {
    console.log(`Servidor escuchando en el puerto ${port}`);
});

app.get("/", (req, res) => {
    res.sendFile(pathHTML + '/index.html');
})

app.get("/publicar", (req, res) => {
    res.sendFile(pathHTML + '/formulario.html');
})

app.get("/publicacion/:idPublicacion", (req, res) => {
    res.sendFile(pathHTML + '/publicacion.html');
    //res.send(req.params)
})

// Middleware para rutas no encontradas (error 404)
app.use((req, res, next) => {
    res.status(404).send(`
      <!DOCTYPE html>
      <html lang="es">
      <head>
          <meta charset="UTF-8">
          <meta name="viewport" content="width=device-width, initial-scale=1.0">
          <title>Error 404 - Página no encontrada</title>
          <style>
              body {
                  font-family: Arial, sans-serif;
                  text-align: center;
                  margin-top: 50px;
              }
              h1 {
                  color: #ff0000;
              }
              p {
                  font-size: 1.2em;
              }
          </style>
      </head>
      <body>
          <h1>Error 404</h1>
          <p>Lo sentimos, la página que buscas no existe.</p>
      </body>
      </html>
    `);
  });
  


