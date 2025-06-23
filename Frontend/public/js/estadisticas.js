import {obtenerURL} from './utils.js'

const backendURL = obtenerURL()

Vue.createApp({
  setup() {
    const estadisticas = Vue.reactive({
      Pokemon: 0,
      YuGiOh: 0,
      Magic: 0,
      Total: 0
    });
    const mensaje = Vue.ref("Cargando estadísticas...")

    Vue.onMounted(() => {
      fetch(backendURL + "/estadisticas")
        .then(res => res.json())
        .then(json => {
            const resultado = json
            estadisticas.Pokemon = resultado["Pokémon"] // maravillas repugnantes de js
            estadisticas.YuGiOh = resultado["Yu-Gi-Oh!"] // maravillas repugnantes de js
            estadisticas.Magic = resultado.Magic // maravillas repugnantes de js
            estadisticas.Total = resultado.Total
            console.log(estadisticas.value)
        })
        .catch(err => {
            mensaje.value = "Error al obtener estadísticas: " + err
            console.error(mensaje.value)
        });
    });

    return {
      estadisticas, 
      mensaje
    };
  }
}).mount('#app')
