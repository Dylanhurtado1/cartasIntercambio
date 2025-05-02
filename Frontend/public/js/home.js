let { ref } = Vue;

const backendURL = "http://localhost:8080"

Vue.createApp({
  setup() {
    const listaDePublicaciones = ref([]);
    const sinDatos = ref("");

    function getPublicaciones() {
      fetch(backendURL + "/publicaciones")
        .then(res => res.json())
        .then(json => {
          listaDePublicaciones.value = json;
          if(listaDePublicaciones.value.length == 0)
            sinDatos.value = "No hay elementos cargados en el sistema :("
        })
        .catch(err =>{
          sinDatos.value = "Error al cargar publicaciones del servidor";
          console.error(sinDatos.value)
        });
    }

    getPublicaciones();

    return { listaDePublicaciones, sinDatos };
  }
}).mount("#app");