const { createApp, ref } = Vue

const backendURL = "http://localhost:8080"; //ahora como modo de prueba, se dejará el link acá 

createApp({
  setup() {
    let listaDePublicaciones = ref([])

    function getPublicaciones(){
        fetch(backendURL + "/publicaciones")
        .then(response => response.json())  // convertir a json
        .then(json => {
            listaDePublicaciones.value = json
            console.log(listaDePublicaciones.value)
        })    
        .catch(err => console.log('Solicitud fallida: ', err)) // Capturar errores
    }

    getPublicaciones()
    
    return {
      listaDePublicaciones: listaDePublicaciones
    }
  }
}).mount('#app');