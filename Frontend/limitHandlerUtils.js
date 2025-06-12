const dayjs = require("dayjs")
const duration = require('dayjs/plugin/duration'); 
dayjs.extend(duration); // que forma más rara de meter extensiones

function limiterHandler(req, res, next, options){

    // si es la primera vez que se bloquea, devuelvo un "too many request", si no que está bloqueado, por dolor de huevos
    const statusCode = isFirstTimeBlocked(res, options.windowMs) ? 429 : 403    
    res.status(statusCode).send(options.message(req, res))
}

function generateMessage (req, res){
    const ip = req.ip
    const statusCode = res.statusCode
    
    let formattedTimeRemaining = getTimeRemaining(res)
    let statusTitle = "Error 403 - usuario no autorizado"
    let statusInfo = `Tiempo restante: <strong>${formattedTimeRemaining}</strong>`

    if(statusCode == 429){
        statusTitle = "Error 429 - Too many request"
        statusInfo = "Usuario bloqueado por hacer muchas request en poco tiempo, vuelva en 5 minutos"
    }

    // Muestra un mensaje HTML con el tiempo restante
    return `
        <!DOCTYPE html>
        <html lang="es">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>${statusTitle}</title>
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
            <h1>${statusTitle}</h1>
            <p>La IP <strong>${ip}</strong> fue bloqueada</p>
            <p>${statusInfo}</p>
        </body>
        </html>
    `;
}

function getTimeRemaining(res) {
    const resetTime = res.getHeader('X-RateLimit-Reset')
    const now = Math.floor(Date.now() / 1000) // Tiempo actual en segundos

    // Calcula el tiempo restante en segundos
    const timeRemaining = resetTime - now

    // Usamos el plugin duration para convertir el tiempo restante
    const durationRemaining = dayjs.duration(timeRemaining, 'seconds')

    const hours = durationRemaining.hours()
    const minutes = durationRemaining.minutes()
    const seconds = durationRemaining.seconds()

    let formattedTimeRemaining = ''

    // sinceramente puse horas por puro reflejo de hacer ejercicios así, sinceramente no creo que llegue a bloquear horas a alguien
    if (hours > 0) {
        formattedTimeRemaining += `${hours} hora${hours > 1 ? 's' : ''}, `
    }

    if (minutes > 0) {
        formattedTimeRemaining += `${minutes} minuto${minutes > 1 ? 's' : ''}, `
    }

    formattedTimeRemaining += `${seconds} segundo${seconds > 1 ? 's' : ''}`
    return formattedTimeRemaining
}

function isFirstTimeBlocked(res, timeMs) {
    const resetTime = res.getHeader('X-RateLimit-Reset')
    const currentTime = Math.floor(Date.now() / 1000)
    const totalTimeSc = timeMs / 1000

    const DELTA = .5 //lo necesito porque no pueden estar 100% sincronizados por diferencias de reloj con el server
    const timeRemaining = resetTime - currentTime
    
    return totalTimeSc - timeRemaining <= DELTA
}

module.exports = {generateMessage, limiterHandler}