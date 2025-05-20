const usuarioLogueado = JSON.parse(localStorage.getItem("usuarioLogueado"));
if (!usuarioLogueado) {
  window.location.href = "/login";
}

let { ref } = Vue;

const backendURL = "http://localhost:8080"

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

    getPublicaciones();

    return { listaDePublicaciones, estadoDeLaCarga, datosBuscador};
  }
}).mount("#app");