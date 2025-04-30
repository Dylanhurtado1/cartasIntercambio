let { ref } = Vue;

const backendURL = "localhost:8080"

Vue.createApp({
  setup() {
    const listaDePublicaciones = ref([]);

    function getPublicaciones() {
      fetch(backendURL + "/publicaciones")
        .then(res => res.json())
        .then(json => {
          listaDePublicaciones.value = json;
          console.log(listaDePublicaciones.value);
        })
        .catch(err => console.error("Error al cargar publicaciones:", err));
    }

    getPublicaciones();

    return { listaDePublicaciones };
  }
}).mount("#app");