const backendURL = "http://44.202.67.120:8080"

Vue.createApp({
  setup() {
    const estadisticas = Vue.ref(null);
    const mensaje = Vue.ref("Cargando estadísticas...")

    Vue.onMounted(() => {
      fetch(backendURL + "/estadisticas")
        .then(res => res.json())
        .then(json => {
            estadisticas.value = json;
        })
        .catch(err => {
            mensaje.value = "Error al obtener estadísticas";
            console.error(mensaje.value);
        });
    });

    return {
      estadisticas, 
      mensaje
    };
  }
}).mount('#app');
