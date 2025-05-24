// Bellísimo, lo sé

function guardarDatoCrudo(key, value){localStorage.setItem(key, value);}

function guardarDatoObjeto(key, value){localStorage.setItem(key, JSON.stringify(value))};

function obtenerDatoCrudo(key){return localStorage.getItem(key);}

function obtenerDatoObjeto(key){return JSON.parse(localStorage.getItem(key))}

function vaciarDatos(){localStorage.clear()}

export {guardarDatoCrudo, guardarDatoObjeto, obtenerDatoCrudo, obtenerDatoObjeto, vaciarDatos};