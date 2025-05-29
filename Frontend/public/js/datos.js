// Bellísimo, lo sé


function guardarDatoCrudo(key, value){localStorage.setItem(key, value);}

function guardarDatoObjeto(key, value){localStorage.setItem(key, JSON.stringify(value))};

function obtenerDatoCrudo(key){return localStorage.getItem(key);}

function obtenerDatoObjeto(key){return JSON.parse(localStorage.getItem(key))}

function vaciarDatos(){localStorage.clear()}

function sesionAbierta(){
    return (obtenerDatoCrudo("jwt") && obtenerDatoCrudo("usuarioActual")) !== null
}

// separación mental

function manejarErrorImagen(event) {
    const img = event.target;

    if (!img.dataset.fallback) {
    img.src = '/public/img/noCard.png';
    img.dataset.fallback = 'true';
    }
}



export {guardarDatoCrudo, guardarDatoObjeto, obtenerDatoCrudo, obtenerDatoObjeto, vaciarDatos, sesionAbierta, manejarErrorImagen};