export function guardarDatoCrudo(key, value){localStorage.setItem(key, value);}

export function guardarDatoObjeto(key, value){localStorage.setItem(key, JSON.stringify(value))};

export function obtenerDatoCrudo(key){return localStorage.getItem(key);}

export function obtenerDatoObjeto(key){return JSON.parse(localStorage.getItem(key))}

//export {guardarDatoCrudo, guardarDatoObjeto, obtenerDatoCrudo, guardarDatoObjeto};