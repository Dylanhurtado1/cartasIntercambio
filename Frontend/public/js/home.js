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
    const sinDatos = ref("");

    function getPublicaciones() {
      fetch(backendURL + "/publicaciones" + queryString)
        .then(res => res.json())
        .then(json => {
          listaDePublicaciones.value = json;
          if(listaDePublicaciones.value.length == 0)
            sinDatos.value = "No se han encontrado elementos en el sistema :("
        })
        .catch(err =>{
          sinDatos.value = "Error al cargar publicaciones del servidor";
          console.error(sinDatos.value)
        });
    }

    getPublicaciones();

    return { listaDePublicaciones, sinDatos, datosBuscador};
  }
}).mount("#app");