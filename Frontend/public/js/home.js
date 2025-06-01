const { ref } = Vue
import {obtenerURL} from './utils.js'

const backendURL = obtenerURL()

Vue.createApp({
  setup() {
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    const datosBuscador = {
      nombre: urlParams.get("nombre"),
      usuario: urlParams.get("usuario"),
      juego: urlParams.get("juego"),
      preciomin: urlParams.get("preciomin"),
      preciomax: urlParams.get("preciomax")
    }

    const listaDePublicaciones = ref([]);
    const estadoDeLaCarga = ref("Cargando publicaciones...");

    function getPublicaciones() {
      fetch(backendURL + "/publicaciones" + queryString)
        .then(res => res.json())
        .then(json => {
          listaDePublicaciones.value = json;
          estadoDeLaCarga.value = ""
          if(listaDePublicaciones.value.length == 0)
            estadoDeLaCarga.value = "No se han encontrado elementos en el sistema :("
        })
        .catch(err =>{
          estadoDeLaCarga.value = "Error al cargar publicaciones del servidor";
          console.error(estadoDeLaCarga.value)
        });
    }

    function manejarErrorImagen(event) {
      const img = event.target;

      if (!img.dataset.fallback) {
        img.src = '/public/img/noCard.png';
        img.dataset.fallback = 'true';
      }
    }

    getPublicaciones();

    return { listaDePublicaciones, estadoDeLaCarga, datosBuscador, manejarErrorImagen};
  }
}).mount("#app");