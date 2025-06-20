import {obtenerURL} from './utils.js'

const backendURL = obtenerURL()

Vue.createApp({
  setup() {
    const estadisticas = Vue.ref(null);
    const mensaje = Vue.ref("Cargando estadísticas...")

    Vue.onMounted(() => {
      fetch(backendURL + "/estadisticas")
        .then(res => res.json())
        .then(json => {
            estadisticas.value = json;
            console.log(json)
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
