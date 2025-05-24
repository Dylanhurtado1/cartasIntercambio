const usuarioLogueado = JSON.parse(localStorage.getItem("usuarioLogueado"));
if (!usuarioLogueado) {
  window.location.href = "/login";
}

const backendURL = "http://localhost:8080"

Vue.createApp({
  setup() {
    const estadisticas = Vue.ref(null);
    const mensaje = Vue.ref("Cargando estadísticas...")

    Vue.onMounted(() => {
      const jwt = localStorage.getItem('jwt');
      fetch(backendURL + "/estadisticas", {
        headers: jwt ? { 'Authorization': 'Bearer ' + jwt } : {}
      })
        .then(res => {
          if (!res.ok) throw new Error("Error al obtener estadísticas: " + res.status);
          return res.json();
        })
        .then(json => {
            estadisticas.value = json;
        })
        .catch(err => {
            mensaje.value = "Error al obtener estadísticas";
            console.error(mensaje.value, err);
        });
    });

    return {
      estadisticas, 
      mensaje
    };
  }
}).mount('#app');
