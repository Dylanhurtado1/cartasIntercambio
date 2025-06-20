const { ref, reactive, watch } = Vue
import {obtenerURL} from './utils.js'
import { Pagination } from "./paginacion.js";

const backendURL = obtenerURL()

Vue.createApp({
  components: { Pagination },
  setup() {
    const datosBuscador = reactive({
      nombre: "",
      estado: "",
      juego: "",
      preciomin: "",
      preciomax: "",
      pageNo: 0
    })

    const paginasTotal = ref(0)
    const listaDePublicaciones = ref([])
    const estadoDeLaCarga = ref("Cargando publicaciones...")

    function toQueryUrl(data) {
      const query = Object.entries(data)
        .filter(([_, value]) => value !== "")
        .map(([key, value]) => `${encodeURIComponent(key)}=${encodeURIComponent(value)}`)
        .join("&")
      return (query ? `?${query}` : "")
    }

    function getPublicaciones() {
      estadoDeLaCarga.value = "Cargando publicaciones..."
      fetch(backendURL + "/publicaciones" + toQueryUrl(datosBuscador))
        .then(res => res.json())
        .then(json => {
          listaDePublicaciones.value = json.content
          paginasTotal.value = json.totalPages
          estadoDeLaCarga.value = listaDePublicaciones.value.length === 0
            ? "No se han encontrado elementos en el sistema :("
            : ""
        })
        .catch(err => {
          listaDePublicaciones.value = []
          estadoDeLaCarga.value = "Error al cargar publicaciones: " + err
        })
    }

    function manejarErrorImagen(event) {
      const img = event.target
      if (!img.dataset.fallback) {
        img.src = '/public/img/noCard.png'
        img.dataset.fallback = 'true'
      }
    }

    watch(() => datosBuscador.pageNo, getPublicaciones)

    getPublicaciones()

    return {
      listaDePublicaciones,
      estadoDeLaCarga,
      datosBuscador,
      paginasTotal,
      getPublicaciones,
      manejarErrorImagen
    }
  }
}).mount("#app")