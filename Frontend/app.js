const {generateMessage, limiterHandler} = require("./limitHandlerUtils")
const path = require('path')
const express = require("express")

const app = express()

const port = 9090
const pathHTML = __dirname + '/views'

app.use('/public', express.static(path.join(__dirname, 'public'))) //tener acceso a los archivos estáticos

// cositas necesarias para el limitar el tiempo
const rateLimit = require('express-rate-limit')


// COMIENZO DE LA BLACKLIST (el intento por el lado de front) //

// sé perfectamente que esta no es la solución más escalabe, pero confío que AWS me va a atajar antes los problemas gordos

const limiterTime = 5 * 60 * 1000 // 5 minutos en milisegundos

const limiter = rateLimit({
    windowMs: limiterTime,
    max: 50,  // si tengo más de 50 solicitudes en menos de 5 minutos, al rincón de la blacklist
    handler: limiterHandler,
    message: generateMessage,
    headers: true,
})

// defiendo a muerte todas las rutas con la blacklist y el rate limiter
app.use(limiter) 


// FIN DE LA BLACKLIST //

const server = app.listen(port, () => {
    console.log(`Servidor escuchando en el puerto ${port}`)
})

app.get("/", (req, res) => {    
    res.sendFile(pathHTML + '/index.html')
})

app.get("/publicar", (req, res) => {
    res.sendFile(pathHTML + '/formulario.html')
})

app.get("/publicacion/:idPublicacion", (req, res) => {
    res.sendFile(pathHTML + '/publicacion.html')
    //res.send(req.params)
})


app.get("/publicacion/:idPublicacion/ofertas", (req, res) => {
    res.sendFile(pathHTML + '/ofertas.html')
    //res.send(req.params)
})

app.get("/estadisticas", (req, res) => {
    res.sendFile(pathHTML + '/estadistica.html')
    //res.send(req.params)
})

app.get("/login", (req, res) => {
    res.sendFile(pathHTML + '/login.html')
})

app.get("/registrar", (req, res) => {
    res.sendFile(pathHTML + '/registro.html')
})

app.get("/usuario", (req, res) => {
    res.sendFile(pathHTML + '/usuario.html')
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
    `)
  })

// Procesamiento de señal para cuando cae el servidor, agregado porque vi que el Docker tarda mucho en terminar el proceso

process.on('SIGTERM', () => {
  console.log('SIGTERM received, shutting down...')
  server.close(() => {
    console.log('Server closed')
    process.exit(0) // Señal de fin
  })
})

process.on('SIGINT', () => {
  console.log('SIGINT received, shutting down...')
  server.close(() => {
    console.log('Server closed')
    process.exit(0) // Señal de fin
  })
})
  


