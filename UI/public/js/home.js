const { createApp, ref } = Vue

const backendURL = "http://localhost:8080"; //ahora como modo de prueba, se dejará el link acá 

createApp({
  setup() {
    let listaDeCartas = ref([])

    function getPublicaciones(){
        fetch(backendURL + "/publicaciones")
        .then(response => response.json())  // convertir a json
        .then(json => {
            listaDeCartas.value = json
        })    
        .catch(err => console.log('Solicitud fallida:', err)); // Capturar errores
    }

    getPublicaciones()
    
    return {
      listaDeCartas
    }
  }
}).mount('#app')