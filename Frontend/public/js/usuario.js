import {obtenerDatoCrudo, getUserData, sesionAbierta, manejarErrorImagen, obtenerURL} from './utils.js'
const { onMounted, watch } = Vue
import { Pagination } from "./paginacion.js"

Vue.createApp({
  components: { Pagination },
  setup() {
    const usuarioActual = getUserData() || "Invitado"
    const usuario = Vue.ref(usuarioActual);
    const publicacionesCargadas = Vue.ref([]);
    const ofertas = Vue.ref([]);

    // Datos para paginación, arrepentido de no hacerlo un objeto y listo
    const paginaActual = Vue.ref(0)
    const paginasTotales = Vue.ref(0)
    const publicacionesTotales = Vue.ref(0)
    const contenidoCargado = Vue.ref(0) // 0 = cargando, 1 = cargó, 2 = error, los enums son para débiles

    const id = getUserData().id
    const backendURL = obtenerURL(); 

    async function fetchDatos() {
      console.log(paginaActual.value)
      contenidoCargado.value = 0
      try {
        const headers = {
          "Authorization": `Bearer ${obtenerDatoCrudo("jwt")}`
        }
 
        const URL = paginaActual.value == 0 ? `${backendURL}/publicaciones/usuario/${id}` : `${backendURL}/publicaciones/usuario/${id}?pageNo=${paginaActual.value}`

        // Obtener publicaciones
        const pubRes = await fetch(URL, { headers })
        if (pubRes.ok) {
          const contenido = await pubRes.json()
          
          contenidoCargado.value = 1

          publicacionesTotales.value = contenido.totalElements
          paginaActual.value = contenido.pageNo
          paginasTotales.value = contenido.totalPages
          publicacionesCargadas.value = contenido.content

        }

        // Obtener ofertas
        const ofertasRes = await fetch(`${backendURL}/publicaciones/usuario/${id}/ofertas/realizadas`, { headers })
        if (ofertasRes.ok) {
          ofertas.value = await ofertasRes.json()
        }

      } catch (err) {
        contenidoCargado.value = 2

        publicacionesCargadas.value = []
        ofertas.value = []

        publicacionesTotales.value = 0
        paginaActual.value = 0
        paginasTotales.value = 0 
        console.error("Error obteniendo datos:", err)
      }
    }

    onMounted(() => {
      if (sesionAbierta()) 
        fetchDatos()
    });

    watch(paginaActual, fetchDatos)

    return { 
      usuario, publicacionesCargadas, ofertas, manejarErrorImagen, 
      paginaActual, paginasTotales, publicacionesTotales, contenidoCargado
    }
  }
}).mount("#app");
