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



